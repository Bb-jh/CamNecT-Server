package CamNecT.CamNecT_Server.domain.profile.controller;

import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateOnboardingRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateProfileTagsRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileStatusResponse;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileResponse;
import CamNecT.CamNecT_Server.domain.profile.service.ProfileService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{profileUserId}")
    public ProfileResponse getUserProfile(@PathVariable Long profileUserId){
        return profileService.getUserProfile(profileUserId);
    }

    @PostMapping("/uploads/presign")
    public PresignUploadResponse presignProfileImageUpload(
            @UserId Long userId,
            @RequestBody @Valid PresignUploadRequest req
    ) {
        return profileService.presignProfileImageUpload(userId, req);
    }

    @PostMapping("/onboarding")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileStatusResponse createOnboarding(
            @UserId Long userId,
            @RequestBody @Valid UpdateOnboardingRequest req
    ) {
        return profileService.createOnboarding(userId, req);
    }

    @PutMapping("/tags")
    public ProfileStatusResponse updateProfileTags(
            @UserId Long userId,
            @RequestBody @Valid UpdateProfileTagsRequest req
    ) {
        return profileService.updateProfileTags(userId, req);
    }

}
