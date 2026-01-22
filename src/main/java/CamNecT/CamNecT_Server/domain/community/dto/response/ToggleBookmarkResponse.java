package CamNecT.CamNecT_Server.domain.community.dto.response;

public record ToggleBookmarkResponse(
        Long postId,
        boolean bookmarked,
        long bookmarkCount
) {}
