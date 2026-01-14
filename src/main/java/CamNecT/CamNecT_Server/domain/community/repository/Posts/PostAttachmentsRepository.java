package CamNecT.CamNecT_Server.domain.community.repository.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Posts.PostAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostAttachmentsRepository extends JpaRepository<PostAttachments, Long> {

    // 상세: 첨부 전체(active만)
    List<PostAttachments> findByPost_IdAndStatusTrueOrderByIdAsc(Long postId);

    // 목록 썸네일 1개(active만, 가장 먼저 저장된 첨부)
    Optional<PostAttachments> findTop1ByPost_IdAndStatusTrueOrderByIdAsc(Long postId);

    // 피드: 여러 postId 첨부 한번에(active만)
    List<PostAttachments> findByPost_IdInAndStatusTrueOrderByPost_IdAscIdAsc(List<Long> postIds);

    // 글 삭제/수정 시 첨부 정리용(물리삭제)
    void deleteByPost_Id(Long postId);
}

