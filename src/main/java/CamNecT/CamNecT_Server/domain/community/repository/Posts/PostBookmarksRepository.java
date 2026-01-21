package CamNecT.CamNecT_Server.domain.community.repository.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Posts.PostBookmarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface PostBookmarksRepository extends JpaRepository<PostBookmarks, Long> {

    boolean existsByPost_IdAndUserId(Long postId, Long userId);

    @Modifying
    void deleteByPost_IdAndUserId(Long postId, Long userId);

    long countByPost_Id(Long postId);
}
