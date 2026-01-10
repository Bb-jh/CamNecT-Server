package CamNecT.CamNecT_Server.domain.portfolio.repository;

import CamNecT.CamNecT_Server.domain.portfolio.dto.PortfolioPreviewDTO;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<PortfolioProject, Long> {

    @Query("SELECT new CamNecT.CamNecT_Server.domain.portfolio.dto.PortfolioPreviewDTO(p.portfolioId, p.title, p.thumbnailUrl, p.updatedAt) " +
            "FROM PortfolioProject p " +
            "WHERE p.userId = :userId")
    List<PortfolioPreviewDTO> findPreviewsByUserId(@Param("userId") Long userId);

}
