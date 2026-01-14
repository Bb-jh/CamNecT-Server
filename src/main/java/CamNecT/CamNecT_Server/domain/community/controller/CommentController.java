package CamNecT.CamNecT_Server.domain.community.controller;

import CamNecT.CamNecT_Server.domain.community.dto.request.CreateCommentRequest;
import CamNecT.CamNecT_Server.domain.community.dto.request.UpdateCommentRequest;
import CamNecT.CamNecT_Server.domain.community.dto.response.CreateCommentResponse;
import CamNecT.CamNecT_Server.domain.community.dto.response.ToggleCommentLikeResponse;
import CamNecT.CamNecT_Server.domain.community.service.CommentService;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<CreateCommentResponse> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest req
    ) {
        return ApiResponse.success(commentService.create(userId, postId, req));
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<Void> update(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentRequest req
    ) {
        commentService.update(userId, commentId, req);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> delete(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long commentId
    ) {
        commentService.delete(userId, commentId);
        return ApiResponse.success(null);
    }

    @PostMapping("/comments/{commentId}/likes")
    public ApiResponse<ToggleCommentLikeResponse> toggleCommentLike(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long commentId
    ) {
        return ApiResponse.success(commentService.toggleLike(userId, commentId));
    }

    // 댓글 목록 조회 (flat list: parentCommentId로 프론트에서 묶기)
    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<CommentService.CommentRow>> list(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(commentService.list(postId, size));
    }
}
