package CamNecT.CamNecT_Server.domain.portfolio.service;

import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioDetailResponse;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioAsset;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioProject;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioAssetRepository;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;

    public List<PortfolioPreviewResponse> portfolioPreview(Long userId) {

        return portfolioRepository.findPreviewsByUserId(userId);

    }

    public PortfolioDetailResponse portfolioDetail(Long userId, Long portfolioId) {

        PortfolioProject portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND)
        );

        List<PortfolioAsset> portfolioAssets = portfolioAssetRepository.findAssetsByPortfolioId(portfolioId);

        Boolean isMine = userId.equals(portfolio.getUserId());

        return new PortfolioDetailResponse(isMine, portfolio, portfolioAssets);

    }
}
