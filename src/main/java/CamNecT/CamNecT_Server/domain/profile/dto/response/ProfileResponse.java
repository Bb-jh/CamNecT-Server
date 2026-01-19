package CamNecT.CamNecT_Server.domain.profile.dto.response;

import CamNecT.CamNecT_Server.domain.certificate.dto.response.CertificateResponse;
import CamNecT.CamNecT_Server.domain.education.dto.response.EducationResponse;
import CamNecT.CamNecT_Server.domain.experience.dto.response.ExperienceResponse;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.users.model.UserProfile;
import CamNecT.CamNecT_Server.global.tag.model.Tag;

import java.util.List;

public record ProfileResponse(
    String name,
    UserProfile profile,
    Integer following,
    Integer follower,
    List<PortfolioPreviewResponse> portfolioProjectList,
    List<EducationResponse> educations,
    List<ExperienceResponse> experience,
    List<CertificateResponse> certificate,
    List<Tag> tags
    //Boolean isFollowing
) {
}
