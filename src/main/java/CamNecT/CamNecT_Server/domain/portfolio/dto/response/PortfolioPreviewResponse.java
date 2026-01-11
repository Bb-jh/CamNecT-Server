package CamNecT.CamNecT_Server.domain.portfolio.dto.response;

import java.time.LocalDateTime;

public record PortfolioPreviewResponse(
        Long portfolioId,
        String title,
        String thumbnailUrl,
        LocalDateTime updatedAt
) {
}