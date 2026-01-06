package CamNecT.CamNecT_Server.domain.Users.repository;

import CamNecT.CamNecT_Server.domain.Users.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
