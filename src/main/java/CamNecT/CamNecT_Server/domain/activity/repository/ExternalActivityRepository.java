package CamNecT.CamNecT_Server.domain.activity.repository;

import CamNecT.CamNecT_Server.domain.activity.model.ExternalActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalActivityRepository extends JpaRepository<ExternalActivity, Long>, ExternalActivityRepositoryCustom {

}