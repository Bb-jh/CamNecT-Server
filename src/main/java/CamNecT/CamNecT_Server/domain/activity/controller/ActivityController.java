package CamNecT.CamNecT_Server.domain.activity.controller;

import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityPreviewResponse;
import CamNecT.CamNecT_Server.domain.activity.model.ActivityCategory;
import CamNecT.CamNecT_Server.domain.activity.service.ActivityService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/club")
    public Page<ActivityPreviewResponse> getClubActivity(
            @UserId Long userId,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "LATEST") String sortType,
            Pageable pageable) {
        return activityService.getActivities(userId, ActivityCategory.CLUB, tagIds, title, sortType, pageable);
    }

    @GetMapping("/study")
    public Page<ActivityPreviewResponse> getStudyActivity(
            @UserId Long userId,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "LATEST") String sortType,
            Pageable pageable) {
        return activityService.getActivities(userId, ActivityCategory.STUDY, tagIds, title, sortType, pageable);
    }

    @GetMapping("/external")
    public Page<ActivityPreviewResponse> getExternalActivity(
            @UserId Long userId,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "LATEST") String sortType,
            Pageable pageable) {
        return activityService.getActivities(userId, ActivityCategory.EXTERNAL, tagIds, title, sortType, pageable);
    }

    @GetMapping("/recruitment")
    public Page<ActivityPreviewResponse> getRecruitmentActivity(
            @UserId Long userId,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "LATEST") String sortType,
            Pageable pageable) {
        return activityService.getActivities(userId, ActivityCategory.RECRUITMENT, tagIds, title, sortType, pageable);
    }



}