package CamNecT.CamNecT_Server.domain.auth.service;

import CamNecT.CamNecT_Server.domain.auth.dto.signup.SignupRequest;
import CamNecT.CamNecT_Server.domain.auth.dto.signup.SignupResponse;
import CamNecT.CamNecT_Server.domain.auth.entity.EmailVerificationToken;
import CamNecT.CamNecT_Server.domain.auth.repository.EmailVerificationTokenRepository;
import CamNecT.CamNecT_Server.global.jwt.TokenUtil;
import CamNecT.CamNecT_Server.domain.users.model.UserStatus;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.users.repository.UserRepository;
import CamNecT.CamNecT_Server.global.common.mail.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;

    @Value("${app.auth.email-verification.expiration-minutes:30}")
    private long expirationMinutes;

    @Value("${app.auth.email-verification.verify-base-url}")
    private String verifyBaseUrl;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        // 1) 필수 약관 체크
        if (!req.agreements().serviceTerms() || !req.agreements().privacyTerms()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TERMS_REQUIRED");
        }

        // 2) 중복 체크
        if (userRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
        }
        if (userRepository.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS");
        }

        // 3) Users 저장 (EMAIL_PENDING)
        Users user = Users.builder()
                .email(req.email())
                .username(req.username())
                .name(req.name())
                .phoneNum(req.phoneNum())
                .passwordHash(passwordEncoder.encode(req.password()))
                .termsServiceAgreed(true)
                .termsPrivacyAgreed(true)
                .emailVerified(false)
                .status(UserStatus.EMAIL_PENDING)
                .build();

        userRepository.save(user);

        // 4) 인증 토큰 저장(해시) + 메일 발송(raw)
        String rawToken = TokenUtil.newToken();
        String tokenHash = TokenUtil.sha256Hex(rawToken);

        EmailVerificationToken token = new EmailVerificationToken(
                user,
                tokenHash,
                LocalDateTime.now().plusMinutes(expirationMinutes)
        );
        tokenRepository.save(token);

        String verifyUrl = UriComponentsBuilder
                .fromHttpUrl(verifyBaseUrl)
                .queryParam("token", rawToken)
                .build()
                .toUriString();

        emailSender.sendEmailVerification(user.getEmail(), verifyUrl);

        return new SignupResponse(user.getUserId(), user.getStatus().name());
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        String hash = TokenUtil.sha256Hex(rawToken);

        EmailVerificationToken token = tokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_TOKEN"));

        if (token.isUsed() || token.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TOKEN_EXPIRED_OR_USED");
        }

        token.markUsed();

        Users user = token.getUser();
        user.markEmailVerified();
        user.changeStatus(UserStatus.ADMIN_PENDING); //가계정 -> 학교 인증 대기
    }
}
