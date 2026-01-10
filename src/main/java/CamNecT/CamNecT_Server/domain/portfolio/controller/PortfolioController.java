package CamNecT.CamNecT_Server.domain.portfolio.controller;

import CamNecT.CamNecT_Server.domain.portfolio.dto.PortfolioPreviewDTO;
import CamNecT.CamNecT_Server.domain.portfolio.dto.request.PortfolioRequest;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioResponse;
import CamNecT.CamNecT_Server.domain.portfolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
@Tag(name = "portfolio-controller", description = "포트폴리오 조회, 작성, 수정 등을 위한 컨트롤러입니다.")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public List<PortfolioPreviewDTO> portfolioPreview (@RequestParam Long userId){

        return portfolioService.portfolioPreview(userId);

    }

}
