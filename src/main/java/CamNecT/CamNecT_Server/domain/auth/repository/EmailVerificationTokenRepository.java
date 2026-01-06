package CamNecT.CamNecT_Server.domain.auth.repository;

import CamNecT.CamNecT_Server.domain.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByTokenHash(String TokenHash);
}
