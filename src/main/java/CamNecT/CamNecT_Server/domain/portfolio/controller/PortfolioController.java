package CamNecT.CamNecT_Server.domain.portfolio.controller;

import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioDetailResponse;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioProject;
import CamNecT.CamNecT_Server.domain.portfolio.service.PortfolioService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public List<PortfolioPreviewResponse> portfolioPreview (@UserId Long userId){
        return portfolioService.portfolioPreview(userId);
    }

    @GetMapping("{portfolioId}")
    public PortfolioDetailResponse portfolioDetail (@UserId Long userId, @PathVariable Long portfolioId) {
        return portfolioService.portfolioDetail(userId, portfolioId);
    }

    @PostMapping
    public PortfolioPreviewResponse createPortfolio (){
        return null;
    }

}