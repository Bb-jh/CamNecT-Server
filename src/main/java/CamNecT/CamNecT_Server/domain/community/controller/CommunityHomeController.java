package CamNecT.CamNecT_Server.domain.community.controller;

import CamNecT.CamNecT_Server.domain.community.dto.response.CommunityHomeResponse;
import CamNecT.CamNecT_Server.domain.community.service.CommunityHomeService;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityHomeController {

    private final CommunityHomeService communityHomeService;

    @GetMapping("/home")
    public ApiResponse<CommunityHomeResponse> home(
            @RequestParam(required = false) Long interestTagId
    ) {
        return ApiResponse.success(communityHomeService.getHome(interestTagId));
    }
}
