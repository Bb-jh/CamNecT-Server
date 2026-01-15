package CamNecT.CamNecT_Server.domain.institution.repository;

import CamNecT.CamNecT_Server.global.tag.model.Majors;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Majors, Long> {


    List<Majors> findByInstitution_InstitutionId(Long institutionId);


    Optional<Majors> findByMajorIdAndInstitution_InstitutionId(
            Long majorId,
            Long institutionId
    );

}
