package CamNecT.CamNecT_Server.domain.activity.repository.external_activity;

import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivityAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalActivityAttachmentRepository extends JpaRepository<ExternalActivityAttachment, Long> {

    void deleteByExternalActivity(Long activityId);

    List<ExternalActivityAttachment> findAllByExternalActivity(Long activityId);
}
