package CamNecT.CamNecT_Server.domain.community.repository.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Posts.PostStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostStatsRepository extends JpaRepository<PostStats, Long> {
    Optional<PostStats> findByPost_Id(Long postId);

    List<PostStats> findByPost_IdIn(Collection<Long> postIds);
}
