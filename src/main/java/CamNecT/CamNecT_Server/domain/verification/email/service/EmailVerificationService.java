package CamNecT.CamNecT_Server.domain.verification.email.service;

import CamNecT.CamNecT_Server.domain.users.model.UserStatus;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.users.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public void verifyEmailCode(Long userId, String rawCode) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        if (user.isEmailVerified()) {
            return; // 이미 인증된 경우 idempotent
        }

        EmailVerificationToken token = tokenRepository.findTopByUserAndUsedAtIsNullOrderByIdDesc(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "NO_ACTIVE_CODE"));

        if (token.isExpired() || token.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CODE_EXPIRED_OR_USED");
        }
        if (token.isLocked()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_ATTEMPTS");
        }

        if (!token.matchesCode(rawCode)) {
            token.increaseAttempt();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_CODE");
        }

        token.markUsed();
        user.markEmailVerified();
        user.changeStatus(UserStatus.ADMIN_PENDING);
    }
}
