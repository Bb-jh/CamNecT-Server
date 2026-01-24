package CamNecT.CamNecT_Server.domain.community.controller;

import CamNecT.CamNecT_Server.domain.community.dto.request.CreatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.request.UpdatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.response.*;
import CamNecT.CamNecT_Server.domain.community.service.PostQueryService;
import CamNecT.CamNecT_Server.domain.community.service.PostQueryService.*;
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
    private final PostQueryService postQueryService;

    @PostMapping
    public ApiResponse<CreatePostResponse> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
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



    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getDetail(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.getDetail(userId, postId));
    }

    //좋아요
    @PostMapping("/{postId}/likes")
    public ApiResponse<ToggleLikeResponse> toggleLike(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.toggleLike(userId, postId));
    }

    //댓글 채택
    @PostMapping("/{postId}/comments/{commentId}/accept")
    public ApiResponse<Void> accept(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postService.acceptComment(userId, postId, commentId);
        return ApiResponse.success(null);
    }

    //북마크
    @PostMapping("/{postId}/bookmarks")
    public ApiResponse<ToggleBookmarkResponse> toggleBookmark(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.toggleBookmark(userId, postId));
    }

    //글 구매
    @PostMapping("/{postId}/access/purchase")
    public ApiResponse<PurchasePostAccessResponse> purchaseAccess(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long postId
    ) {
        return ApiResponse.success(postService.purchasePostAccess(userId, postId));
    }
}