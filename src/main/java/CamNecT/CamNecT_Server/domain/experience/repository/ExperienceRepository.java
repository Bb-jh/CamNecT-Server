package CamNecT.CamNecT_Server.domain.experience.repository;

import CamNecT.CamNecT_Server.domain.experience.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findAllByUserIdOrderByStartDateDesc(Long userId);
}