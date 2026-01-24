package CamNecT.CamNecT_Server.domain.community.repository.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Posts.PostAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostAccessRepository extends JpaRepository<PostAccess, Long> {
    boolean existsByPost_IdAndUserId(Long postId, Long userId);
}
