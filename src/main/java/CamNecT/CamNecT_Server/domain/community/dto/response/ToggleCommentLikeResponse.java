package CamNecT.CamNecT_Server.domain.community.dto.response;

public record ToggleCommentLikeResponse(
        boolean liked,
        long likeCount
) {}
