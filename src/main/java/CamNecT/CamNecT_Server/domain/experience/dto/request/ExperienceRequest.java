package CamNecT.CamNecT_Server.domain.experience.dto.request;

import java.time.LocalDate;

public record ExperienceRequest(
        Long userId,
        String companyName,
        String majorName,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        String description
) {
}