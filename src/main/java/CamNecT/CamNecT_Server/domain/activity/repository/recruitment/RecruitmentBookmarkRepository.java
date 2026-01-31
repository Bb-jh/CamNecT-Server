package CamNecT.CamNecT_Server.domain.activity.repository.recruitment;

import CamNecT.CamNecT_Server.domain.activity.model.recruitment.RecruitmentBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruitmentBookmarkRepository extends JpaRepository<RecruitmentBookmark, Long> {

    Optional<RecruitmentBookmark> findByUserIdAndRecruitId(Long userId, Long recruitId);

    boolean existsByUserIdAndRecruitId(Long userId, Long recruitId);
}
