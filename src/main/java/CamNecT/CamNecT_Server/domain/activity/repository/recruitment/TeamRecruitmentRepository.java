package CamNecT.CamNecT_Server.domain.activity.repository.recruitment;

import CamNecT.CamNecT_Server.domain.activity.model.recruitment.TeamRecruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRecruitmentRepository extends JpaRepository<TeamRecruitment, Long> {

    List<TeamRecruitment> findAllByActivityId(Long activityId);
}
