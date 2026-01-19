package CamNecT.CamNecT_Server.domain.experience.dto.response;

import CamNecT.CamNecT_Server.domain.experience.model.Experience;

import java.time.LocalDate;

public record ExperienceResponse(
        Long userId,
        String companyName,
        String majorName,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        String description
) {
    public static ExperienceResponse from(Experience experience) {
        return new ExperienceResponse(
                experience.getExperienceId(),
                experience.getCompanyName(),
                experience.getMajorName(),
                experience.getStartDate(),
                experience.getEndDate(),
                experience.getIsCurrent(),
                experience.getDescription()
        );
    }
}
