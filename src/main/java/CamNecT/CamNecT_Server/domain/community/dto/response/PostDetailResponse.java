package CamNecT.CamNecT_Server.domain.community.dto.response;

import CamNecT.CamNecT_Server.domain.community.model.enums.BoardCode;

import java.util.List;

public record PostDetailResponse(
        Long postId,
        BoardCode boardCode,
        String title,
        String content,
        boolean anonymous,
        Long authorId,
        long viewCount,
        long likeCount,
        boolean likedByMe,
        Long acceptedCommentId,
        List<Long> tagIds
) {}
