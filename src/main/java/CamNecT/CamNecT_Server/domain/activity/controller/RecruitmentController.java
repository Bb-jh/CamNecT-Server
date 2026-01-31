package CamNecT.CamNecT_Server.domain.activity.controller;

import CamNecT.CamNecT_Server.domain.activity.dto.request.RecruitmentApplyRequest;
import CamNecT.CamNecT_Server.domain.activity.dto.request.RecruitmentRequest;
import CamNecT.CamNecT_Server.domain.activity.dto.response.RecruitmentDetailResponse;
import CamNecT.CamNecT_Server.domain.activity.model.recruitment.TeamRecruitment;
import CamNecT.CamNecT_Server.domain.activity.service.RecruitmentService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activity/{activityId}/recruitment")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;


    @PostMapping
    public ApiResponse<TeamRecruitment> createRecruitment(
            @UserId Long userId,
            @PathVariable Long activityId,
            @RequestBody RecruitmentRequest request
    ){
        return ApiResponse.success(recruitmentService.createRecruitment(userId, activityId, request));
    }

    //todo : 팀원 모집 조회 구현
    @GetMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentDetailResponse> getRecruitmentDetail(
            @UserId Long userId,
            @PathVariable Long activityId,
            @PathVariable Long recruitmentId
    ){
        return ApiResponse.success(recruitmentService.getRecruitmentDetail(userId, recruitmentId));
    }

    //todo : 북마크 구현
    @PostMapping("/{recruitmentId}/bookmark")
    public ApiResponse<String> toggleBookmark(
            @UserId Long userId,
            @PathVariable Long activityId,
            @PathVariable Long recruitmentId
    ) {
        boolean isBookmarked = recruitmentService.toggleRecruitmentBookmark(userId, recruitmentId);
        String message = isBookmarked ? "북마크가 등록되었습니다." : "북마크가 해제되었습니다.";
        return ApiResponse.success(message);
    }

    @PostMapping("/{recruitmentId}/apply")
    public ApiResponse<Long> applyToTeam(
            @UserId Long userId,
            @PathVariable Long activityId,
            @PathVariable Long recruitmentId,
            @RequestBody RecruitmentApplyRequest request
    ) {
        Long applicationId = recruitmentService.applyToTeam(userId, recruitmentId, request);
        return ApiResponse.success(applicationId);
    }

    //todo : 팀원 모집 신청 조회(리스트 보기 & 상세보기) 구현


    //todo : 팀원 승인/삭제 구현


    //todo : 일괄 삭제 구현

}
