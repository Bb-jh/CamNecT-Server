package CamNecT.CamNecT_Server.domain.portfolio.dto.response;

import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioProject;

import java.util.List;

public record PortfolioResponse(
    List<PortfolioProject> portfolioProjectList
) {
}
