package CamNecT.CamNecT_Server.global.storage.controller;

import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import CamNecT.CamNecT_Server.global.storage.service.bydomain.CommunityAttachmentPresignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class PresignController {

    private final CommunityAttachmentPresignService communityPresignService;

    @PostMapping("/presign/community-attachment")
    public ApiResponse<PresignUploadResponse> presignCommunityAttachment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid PresignUploadRequest req
    ) {
        return ApiResponse.success(communityPresignService.presign(userId, req));
    }
}