package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.dto.request.CreatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.request.UpdatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.response.CreatePostResponse;
import CamNecT.CamNecT_Server.domain.community.dto.response.PostDetailResponse;
import CamNecT.CamNecT_Server.domain.community.dto.response.ToggleBookmarkResponse;
import CamNecT.CamNecT_Server.domain.community.dto.response.ToggleLikeResponse;

public interface PostService {
    CreatePostResponse create(Long userId, CreatePostRequest req);

    void update(Long userId, Long postId, UpdatePostRequest req);

    void delete(Long userId, Long postId);

    ToggleLikeResponse toggleLike(Long userId, Long postId);

    PostDetailResponse getDetail(Long userId, Long postId);

    void acceptComment(Long userId, Long postId, Long commentId);

    ToggleBookmarkResponse toggleBookmark(Long userId, Long postId);
}
