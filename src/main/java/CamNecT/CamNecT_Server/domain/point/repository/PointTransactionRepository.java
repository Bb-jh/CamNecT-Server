package CamNecT.CamNecT_Server.domain.point.repository;

import CamNecT.CamNecT_Server.domain.point.model.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    boolean existsByEventKey(String eventKey);
}
