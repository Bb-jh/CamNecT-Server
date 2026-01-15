package CamNecT.CamNecT_Server.domain.education.controller;

import CamNecT.CamNecT_Server.domain.education.dto.request.EducationRequest;
import CamNecT.CamNecT_Server.domain.education.service.EducationService;
import CamNecT.CamNecT_Server.domain.users.model.CustomUserDetails;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    // 학력 추가
    @PostMapping
    public ApiResponse<Void> addEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid EducationRequest request
    ) {
        educationService.addEducation(userDetails.getUserId(), request);
        return ApiResponse.success(null);
    }

    // 학력 수정
    @PatchMapping("/{educationId}")
    public ApiResponse<Void> updateEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long educationId,
            @RequestBody @Valid EducationRequest request
    ) {
        educationService.updateEducation(userDetails.getUserId(), educationId, request);
        return ApiResponse.success(null);
    }

    // 학력 삭제
    @DeleteMapping("/{educationId}")
    public ApiResponse<Void> deleteEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long educationId
    ) {
        educationService.deleteEducation(userDetails.getUserId(), educationId);
        return ApiResponse.success(null);
    }
}

