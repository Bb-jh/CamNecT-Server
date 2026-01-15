package CamNecT.CamNecT_Server.domain.profile.dto.response;

import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.users.model.UserProfile;
import CamNecT.CamNecT_Server.global.tag.model.Certificate;
import CamNecT.CamNecT_Server.global.tag.model.Experience;
import CamNecT.CamNecT_Server.global.tag.model.Tag;

import java.util.List;

public record ProfileResponse(
    String name,
    UserProfile profile,
    Integer following,
    Integer follower,
    List<PortfolioPreviewResponse> portfolioProjectList,
    List<Experience> experience,
    List<Certificate> certificate,
    List<Tag> tags
    //Boolean isFollowing
) {
}
