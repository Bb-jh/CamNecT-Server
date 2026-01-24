package CamNecT.CamNecT_Server.domain.portfolio.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public record PortfolioRequest(
        String projectTitle,
        String description,
        LocalDate startedAt,
        LocalDate endedAt,
        String project_role,
        String review,
        MultipartFile thumbnailUrl,
        List<MultipartFile> attachments
) {
}
