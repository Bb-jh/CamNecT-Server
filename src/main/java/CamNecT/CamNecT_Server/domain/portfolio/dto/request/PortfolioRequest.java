package CamNecT.CamNecT_Server.domain.portfolio.dto.request;

import java.time.LocalDate;
import java.util.List;

public record PortfolioRequest(
        String projectTitle,
        String description,
        LocalDate startedAt,
        LocalDate endedAt,
        String project_role,
        String review,
        String thumbnailKey,
        List<String> attachmentKeys
) {
}
