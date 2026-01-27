package CamNecT.CamNecT_Server.domain.activity.service;

import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityPreviewResponse;
import CamNecT.CamNecT_Server.domain.activity.model.ActivityCategory;
import CamNecT.CamNecT_Server.domain.activity.repository.ExternalActivityRepository;
import CamNecT.CamNecT_Server.domain.activity.repository.ExternalActivityTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityService {

    private final ExternalActivityRepository activityRepository;
    private final ExternalActivityTagRepository tagRepository;

    public Page<ActivityPreviewResponse> getActivities(
            Long userId, ActivityCategory category, List<Long> tagIds,
            String title, String sortType, Pageable pageable) {

        return activityRepository.findAllByFilters(userId, category, tagIds, title, sortType, pageable);
    }
}