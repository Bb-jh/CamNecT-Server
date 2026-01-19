package CamNecT.CamNecT_Server.global.tag.repository;

import CamNecT.CamNecT_Server.global.tag.model.Institutions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface InstitutionRepository extends JpaRepository<Institutions, Long> {
    List<Institutions> findAllByOrderByInstitutionNameKorAsc()();
}
