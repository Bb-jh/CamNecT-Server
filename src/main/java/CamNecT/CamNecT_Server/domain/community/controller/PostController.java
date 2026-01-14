package CamNecT.CamNecT_Server.domain.community.controller;

import CamNecT.CamNecT_Server.domain.community.dto.request.CreatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.request.UpdatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.response.CreatePostResponse;
import CamNecT.CamNecT_Server.domain.community.dto.response.ToggleLikeResponse;
import CamNecT.CamNecT_Server.domain.community.service.PostService;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ApiResponse<CreatePostResponse> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody @Valid CreatePostRequest req
    ) {
        return ApiResponse.success(postService.create(userId, req));
    }

    @PatchMapping("/{postId}")
    public ApiResponse<Void> update(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid UpdatePostRequest req
    ) {
        postService.update(userId, postId, req);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId
    ) {
        postService.delete(userId, postId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{postId}/likes")
    public ApiResponse<ToggleLikeResponse> toggleLike(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.toggleLike(userId, postId));
    }

    @PostMapping("/{postId}/views")
    public ApiResponse<Void> view(@PathVariable Long postId) {
        postService.increaseView(postId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{postId}/comments/{commentId}/accept")
    public ApiResponse<Void> accept(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postService.acceptComment(userId, postId, commentId);
        return ApiResponse.success(null);
    }
}