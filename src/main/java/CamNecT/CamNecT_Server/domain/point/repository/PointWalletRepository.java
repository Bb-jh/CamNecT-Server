package CamNecT.CamNecT_Server.domain.point.repository;

import CamNecT.CamNecT_Server.domain.point.model.PointWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointWalletRepository extends JpaRepository<PointWallet, Long> {

    Optional<PointWallet> findByUserId(Long userId);
}
