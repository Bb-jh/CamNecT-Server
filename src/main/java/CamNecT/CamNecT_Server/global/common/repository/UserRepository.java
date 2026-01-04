package CamNecT.CamNecT_Server.global.common.repository;

import CamNecT.CamNecT_Server.global.common.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
