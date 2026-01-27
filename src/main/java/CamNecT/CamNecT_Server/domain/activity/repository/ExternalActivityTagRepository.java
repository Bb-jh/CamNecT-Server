package CamNecT.CamNecT_Server.domain.activity.repository;

import CamNecT.CamNecT_Server.domain.activity.model.ExternalActivityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExternalActivityTagRepository extends JpaRepository<ExternalActivityTag, Long> {
    // 특정 활동에 속한 태그들을 가져올 때 사용
    List<ExternalActivityTag> findByExternalActivity_ActivityId(Long activityId);
}