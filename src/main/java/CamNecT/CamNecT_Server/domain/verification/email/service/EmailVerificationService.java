package CamNecT.CamNecT_Server.domain.verification.email.service;

import CamNecT.CamNecT_Server.domain.users.model.UserStatus;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.verification.email.model.EmailVerificationToken;
import CamNecT.CamNecT_Server.domain.verification.email.repository.EmailVerificationTokenRepository;
import CamNecT.CamNecT_Server.global.jwt.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;

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
