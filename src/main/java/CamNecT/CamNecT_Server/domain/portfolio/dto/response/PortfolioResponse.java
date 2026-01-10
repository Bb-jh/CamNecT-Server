package CamNecT.CamNecT_Server.domain.portfolio.dto.response;

import java.util.List;

public record PortfolioResponse<T>(
    List<T> data
) {
}
