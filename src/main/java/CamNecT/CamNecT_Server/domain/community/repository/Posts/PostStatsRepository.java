package CamNecT.CamNecT_Server.domain.community.repository.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Posts.PostStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostStatsRepository extends JpaRepository<PostStats, Long> {
    Optional<PostStats> findByPost_Id(Long postId);
}
