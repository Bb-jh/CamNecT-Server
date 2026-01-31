package CamNecT.CamNecT_Server.domain.activity.repository.external_activity;

import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityPreviewResponse;
import CamNecT.CamNecT_Server.domain.activity.model.enums.ActivityCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ExternalActivityRepositoryCustom {
    Slice<ActivityPreviewResponse> findActivitiesByCondition(
            Long userId,
            ActivityCategory category,
            List<Long> tagIds,
            String title,
            String sortType,
            Pageable pageable
    );
}