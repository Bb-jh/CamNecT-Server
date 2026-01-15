package CamNecT.CamNecT_Server.domain.institution.repository;

import CamNecT.CamNecT_Server.global.tag.model.Institutions;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InstitutionRepository extends JpaRepository<Institutions, Long> {
}
