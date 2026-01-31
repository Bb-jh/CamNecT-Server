package CamNecT.CamNecT_Server.domain.community.controller;

import CamNecT.CamNecT_Server.domain.community.dto.request.CreatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.request.UpdatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.response.*;
import CamNecT.CamNecT_Server.domain.community.service.PostAttachmentDownloadService;
import CamNecT.CamNecT_Server.domain.community.service.PostAttachmentsService;
import CamNecT.CamNecT_Server.domain.community.service.PostQueryService;
import CamNecT.CamNecT_Server.domain.community.service.PostQueryService.*;
import CamNecT.CamNecT_Server.domain.community.service.PostService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignDownloadResponse;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;
    private final PostQueryService postQueryService;
    private final PostAttachmentDownloadService postAttachmentDownloadService;
    private final PostAttachmentsService postAttachmentsService;

    @PostMapping
    public ApiResponse<CreatePostResponse> create(
            @UserId Long userId,
            @RequestBody @Valid CreatePostRequest req
    ) {
        return ApiResponse.success(postService.create(userId, req));
    }

    //리스트: /api/community/posts
    @GetMapping
    public ApiResponse<PostListResponse> list(
            @RequestParam(defaultValue = "ALL") Tab tab,
            @RequestParam(defaultValue = "RECOMMENDED") Sort sort,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Long cursorValue,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(postQueryService.getPosts(tab, sort, tagId, keyword, cursorId, cursorValue, size));
    }

    @PatchMapping("/{postId}")
    public ApiResponse<Void> update(
            @UserId Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid UpdatePostRequest req
    ) {
        postService.update(userId, postId, req);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @UserId Long userId,
            @PathVariable Long postId
    ) {
        postService.delete(userId, postId);
        return ApiResponse.success(null);
    }



    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getDetail(
            @UserId Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.getDetail(userId, postId));
    }

    //좋아요
    @PostMapping("/{postId}/likes")
    public ApiResponse<ToggleLikeResponse> toggleLike(
            @UserId Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.toggleLike(userId, postId));
    }

    //댓글 채택
    @PostMapping("/{postId}/comments/{commentId}/accept")
    public ApiResponse<Void> accept(
            @UserId Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postService.acceptComment(userId, postId, commentId);
        return ApiResponse.success(null);
    }

    //북마크
    @PostMapping("/{postId}/bookmarks")
    public ApiResponse<ToggleBookmarkResponse> toggleBookmark(
            @UserId Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.toggleBookmark(userId, postId));
    }

    //글 구매
    @PostMapping("/{postId}/access/purchase")
    public ApiResponse<PurchasePostAccessResponse> purchaseAccess(
            @UserId Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.purchasePostAccess(userId, postId));
    }

    @PostMapping("/uploads/presign")
    public ApiResponse<PresignUploadResponse> presignAttachmentUpload(
            @UserId Long userId,
            @RequestBody @Valid PresignUploadRequest req
    ) {
        return ApiResponse.success(postAttachmentsService.presign(userId, req));
    }

    @GetMapping("/{postId}/attachments/{attachmentId}/download-url")
    public ApiResponse<PresignDownloadResponse> downloadUrl(
            @UserId Long userId,
            @PathVariable Long postId,
            @PathVariable Long attachmentId,
            @RequestParam(defaultValue = "FILE") PostAttachmentDownloadService.Kind kind
    ) {
        return ApiResponse.success(
                postAttachmentDownloadService.presignDownload(userId, postId, attachmentId, kind)
        );
    }
}