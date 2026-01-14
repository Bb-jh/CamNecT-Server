package CamNecT.CamNecT_Server.domain.community.repository.Comments;

import CamNecT.CamNecT_Server.domain.community.model.Comments.AcceptedComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcceptedCommentsRepository extends JpaRepository<AcceptedComments, Long> {

    boolean existsByPost_Id(Long postId);

    Optional<AcceptedComments> findByPost_Id(Long postId);

    void deleteByPost_Id(Long postId);
}
