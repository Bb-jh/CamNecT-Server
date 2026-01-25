package CamNecT.CamNecT_Server.domain.auth.service;

import CamNecT.CamNecT_Server.domain.auth.dto.signup.SignupRequest;
import CamNecT.CamNecT_Server.domain.auth.dto.signup.SignupResponse;
import CamNecT.CamNecT_Server.domain.verification.email.event.EmailVerificationCodeIssuedEvent;
import CamNecT.CamNecT_Server.domain.verification.email.model.EmailTokenUtil;
import CamNecT.CamNecT_Server.domain.verification.email.model.EmailVerificationToken;
import CamNecT.CamNecT_Server.domain.verification.email.repository.EmailVerificationTokenRepository;
import CamNecT.CamNecT_Server.domain.users.model.UserStatus;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.users.repository.UserRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${app.auth.email-verification.expiration-minutes:30}")
    private long expirationMinutes;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        if (!req.agreements().serviceTerms() || !req.agreements().privacyTerms()) {
            throw new CustomException(AuthErrorCode.TERMS_REQUIRED);
        }

        if (userRepository.existsByEmail(req.email())) {
            throw new CustomException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUsername(req.username())) {
            throw new CustomException(AuthErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 3)비밀번호 체크
        validatePassword(req.password());

        // 4) Users 저장 (EMAIL_PENDING)
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

        // 미사용 코드가 남아있으면 정리 (재발급/중복 발급 대비)
        tokenRepository.deleteByUserAndUsedAtIsNull(user);

        String rawCode = EmailTokenUtil.new6DigitCode();
        EmailVerificationToken token = EmailVerificationToken.issue(user, rawCode, expirationMinutes);
        tokenRepository.save(token);

        applicationEventPublisher.publishEvent(
                new EmailVerificationCodeIssuedEvent(user.getEmail(), rawCode, expirationMinutes)
        );

        return new SignupResponse(user.getUserId(), user.getStatus().name());
    }


    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.{8,16}$)" +            // 8~16
                    "(?=.*[A-Z])" +              // 대문자 1+
                    "(?=.*[a-z])" +              // 소문자 1+
                    "(?=.*\\d)" +                // 숫자 1+
                    "[A-Za-z\\d!@#$%^&*()_+\\[\\]{}\\\\|;:'\",.<>/?`~=-]+$" // 허용문자(ASCII 키보드)
    );

    private void validatePassword(String pw) {
        if (pw == null || !PASSWORD_PATTERN.matcher(pw).matches()) {
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }
    }
}
