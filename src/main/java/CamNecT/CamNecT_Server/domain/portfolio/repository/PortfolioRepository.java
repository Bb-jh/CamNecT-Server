package CamNecT.CamNecT_Server.domain.portfolio.repository;

import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<PortfolioProject, Long> {



}
