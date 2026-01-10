package CamNecT.CamNecT_Server.domain.portfolio.service;

import CamNecT.CamNecT_Server.domain.portfolio.dto.PortfolioPreviewDTO;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioResponse;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public List<PortfolioPreviewDTO> portfolioPreview(Long userId) {

        return portfolioRepository.findPreviewsByUserId(userId);

    }
}
