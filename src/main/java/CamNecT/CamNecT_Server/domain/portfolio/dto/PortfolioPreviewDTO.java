package CamNecT.CamNecT_Server.domain.portfolio.dto;

import java.time.LocalDateTime;

public record PortfolioPreviewDTO(
        Long portfolioId,
        String title,
        String thumbnailUrl,
        LocalDateTime updatedAt
) {
}
