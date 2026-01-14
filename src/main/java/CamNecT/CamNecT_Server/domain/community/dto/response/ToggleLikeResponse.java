package CamNecT.CamNecT_Server.domain.community.dto.response;

public record ToggleLikeResponse(
        boolean liked,
        long likeCount
) {}
