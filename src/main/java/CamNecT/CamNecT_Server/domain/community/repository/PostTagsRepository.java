package CamNecT.CamNecT_Server.domain.community.repository;

import CamNecT.CamNecT_Server.domain.community.model.PostTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagsRepository extends JpaRepository<PostTags, Long> {
    List<PostTags> findByPost_Id(Long postId);
    boolean existsByPost_IdAndTag_Id(Long postId, Long tagId);
    void deleteByPost_Id(Long postId);
}
