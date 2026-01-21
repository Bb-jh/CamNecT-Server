package CamNecT.CamNecT_Server.domain.profile.controller;

import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateProfileTagsRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateProfileBasicsRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileStatusResponse;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileResponse;
import CamNecT.CamNecT_Server.domain.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{profileUserId}")
    public ProfileResponse getUserProfile(@PathVariable Long profileUserId){

        return profileService.getUserProfile(profileUserId);

    }

    @PatchMapping("/basics")
    public ProfileStatusResponse updateBasicsSettings(
            @RequestParam Long userId, // TODO: @LoginUser로 교체
            @RequestBody @Valid UpdateProfileBasicsRequest req
    ) {
        return profileService.updateBasicsSettings(userId, req);
    }

    @PutMapping("/tags")
    public ProfileStatusResponse updateProfileTags(
            @RequestParam Long userId, // TODO: @LoginUser로 교체
            @RequestBody @Valid UpdateProfileTagsRequest req
    ) {
        return profileService.updateProfileTags(userId, req);
    }

}
