package CamNecT.CamNecT_Server.domain.experience.controller;

import CamNecT.CamNecT_Server.domain.experience.dto.request.ExperienceRequest;
import CamNecT.CamNecT_Server.domain.experience.dto.response.ExperienceResponse;
import CamNecT.CamNecT_Server.domain.experience.service.ExperienceService;
import CamNecT.CamNecT_Server.domain.users.model.CustomUserDetails;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/me/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    // 학력 조회
    @GetMapping
    public ApiResponse<List<ExperienceResponse>> getMyExperiences(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ExperienceResponse> response = experienceService.getMyExperience(userDetails.getUserId());
        return ApiResponse.success(response);
    }

    // 학력 추가
    @PostMapping
    public ApiResponse<Void> addExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ExperienceRequest request
    ) {
        experienceService.addExperience(userDetails.getUserId(), request);
        return ApiResponse.success(null);
    }

    // 학력 수정
    @PatchMapping("/{experienceId}")
    public ApiResponse<Void> updateExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId,
            @RequestBody @Valid ExperienceRequest request
    ) {
        experienceService.updateExperience(userDetails.getUserId(), experienceId, request);
        return ApiResponse.success(null);
    }

    // 학력 삭제
    @DeleteMapping("/{experienceId}")
    public ApiResponse<Void> deleteExperience(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long experienceId
    ) {
        experienceService.deleteExperience(userDetails.getUserId(), experienceId);
        return ApiResponse.success(null);
    }
}