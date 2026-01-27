package CamNecT.CamNecT_Server.domain.activity.repository;

import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityPreviewResponse;
import CamNecT.CamNecT_Server.domain.activity.model.ActivityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExternalActivityRepositoryCustom {
    Page<ActivityPreviewResponse> findAllByFilters(
            Long userId,
            ActivityCategory category,
            List<Long> tagIds,
            String title,
            String sortType,
            Pageable pageable
    );
}