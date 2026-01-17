package CamNecT.CamNecT_Server.domain.community.repository.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Posts.PostAttachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostAttachmentsRepository extends JpaRepository<PostAttachments, Long> {

    // 상세: 첨부 전체(active만)
    List<PostAttachments> findByPost_IdAndStatusTrueOrderBySortOrderAscIdAsc(Long postId);

    // 목록 썸네일 1개(active만, 가장 먼저 저장된 첨부)
    Optional<PostAttachments> findTop1ByPost_IdAndStatusTrueOrderBySortOrderAscIdAsc(Long postId);

    // 피드: 여러 postId 첨부 한번에(active만)
    @Query("""
           select a from PostAttachments a
           where a.post.id in :postIds and a.status = true
           order by a.post.id asc, a.sortOrder asc, a.id asc
           """)
    List<PostAttachments> findActiveByPostIds(@Param("postIds") List<Long> postIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update PostAttachments a
              set a.status = false
            where a.post.id = :postId
              and a.status = true
           """)
    int softDeleteByPostId(@Param("postId") Long postId);
}

