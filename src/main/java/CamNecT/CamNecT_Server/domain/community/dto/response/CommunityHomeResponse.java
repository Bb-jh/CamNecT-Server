package CamNecT.CamNecT_Server.domain.community.dto.response;

import java.util.List;

public record CommunityHomeResponse(
        Long interestTagId,
        List<PostSummaryResponse> recommendedByInterest,
        List<PostSummaryResponse> waitingQuestions
) {}
