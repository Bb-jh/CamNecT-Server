package CamNecT.CamNecT_Server.domain.activity.controller;

import CamNecT.CamNecT_Server.domain.activity.dto.request.ActivityRequest;
import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityDetailResponse;
import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityPreviewResponse;
import CamNecT.CamNecT_Server.domain.activity.model.enums.ActivityCategory;
import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivity;
import CamNecT.CamNecT_Server.domain.activity.service.ActivityService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    //todo : s3 로직 도입

    @GetMapping
    public Slice<ActivityPreviewResponse> getActivities(
            @UserId Long userId,
            @RequestParam(required = false) ActivityCategory category,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "LATEST") String sortType,
            Pageable pageable) {
        return activityService.getActivities(userId, category, tagIds, title, sortType, pageable);
    }

    @PostMapping
    public ActivityPreviewResponse create(
            @UserId Long userId,
            @RequestBody ActivityRequest request
    ) {
        ExternalActivity activity = activityService.create(userId, request);

        return new ActivityPreviewResponse(
                activity.getActivityId(),
                activity.getTitle(),
                activity.getContext(),
                activity.getThumbnailUrl(),
                null
        );
    }

    @PatchMapping("/{activityId}")
    public ApiResponse<String> update(
            @UserId Long userId,
            @PathVariable Long activityId,
            @RequestBody ActivityRequest request
    ) {
        activityService.update(userId, activityId, request);

        return ApiResponse.success("수정이 완료되었습니다.");
    }

    @GetMapping("/{activityId}")
    public ApiResponse<ActivityDetailResponse> getActivityDetail(
            @UserId Long userId,
            @PathVariable Long activityId
    ) {
        return ApiResponse.success(activityService.getActivityDetail(userId, activityId));
    }

    @DeleteMapping("/{activityId}")
    public ApiResponse<Long> delete(
            @PathVariable Long activityId,
            @UserId Long userId
    ) {
        activityService.delete(activityId, userId);
        return ApiResponse.success(activityId);
    }

    @PostMapping("/{activityId}/bookmark")
    public ApiResponse<String> toggleBookmark(
            @UserId Long userId,
            @PathVariable Long activityId
    ) {
        // true: 북마크 등록됨, false: 북마크 해제됨
        boolean isBookmarked = activityService.toggleActivityBookmark(userId, activityId);
        String message = isBookmarked ? "북마크가 등록되었습니다." : "북마크가 해제되었습니다.";

        return ApiResponse.success(message);
    }

    //todo : 썸네일 업로드 & 첨부파일 업로드 로직 구현


}