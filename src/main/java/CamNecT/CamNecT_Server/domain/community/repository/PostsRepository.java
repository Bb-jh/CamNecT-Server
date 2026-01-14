package CamNecT.CamNecT_Server.domain.community.repository;

import CamNecT.CamNecT_Server.domain.community.model.Posts;
import CamNecT.CamNecT_Server.domain.community.model.enums.BoardCode;
import CamNecT.CamNecT_Server.domain.community.model.enums.PostStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    // =========================
    // Feed (cursor pagination)
    // =========================

    // ALL 탭: 최신순
    Slice<Posts> findByStatusOrderByIdDesc(PostStatus status, Pageable pageable);
    Slice<Posts> findByStatusAndIdLessThanOrderByIdDesc(PostStatus status, Long cursorId, Pageable pageable);

    // INFO / QUESTION 탭: 게시판 필터 + 최신순
    Slice<Posts> findByStatusAndBoard_CodeOrderByIdDesc(PostStatus status, BoardCode code, Pageable pageable);
    Slice<Posts> findByStatusAndBoard_CodeAndIdLessThanOrderByIdDesc(PostStatus status, BoardCode code, Long cursorId, Pageable pageable);

    // =========================
    // Featured (상단 카드)
    // =========================
    List<Posts> findTop5ByStatusOrderByIdDesc(PostStatus status);
    List<Posts> findTop5ByStatusAndBoard_CodeOrderByIdDesc(PostStatus status, BoardCode code);

    // =========================
    // Waiting Questions (답변대기)
    // commentCount == 0 기준 (Posts에 commentCount 컬럼 있어야 함)
    // =========================
    List<Posts> findTop3ByStatusAndBoard_CodeAndCommentCountOrderByIdDesc(
            PostStatus status, BoardCode code, long commentCount
    );

    // =========================
    // Popular/Hot (인기글/추천글)
    // Posts에 hotScore 컬럼이 있을 때 사용
    // =========================
    List<Posts> findTop5ByStatusOrderByHotScoreDescIdDesc(PostStatus status);
    List<Posts> findTop5ByStatusAndBoard_CodeOrderByHotScoreDescIdDesc(PostStatus status, BoardCode code);

    // =========================
    // Tag-based recommendation (태그별 추천)
    // PostTags(조인엔티티), Tag가 있어야 함
    // Posts에 hotScore 컬럼이 있을 때 사용
    // =========================
    @Query("""
        select p from Posts p
          join PostTags pt on pt.post = p
          join Tag t on pt.tag = t
         where p.status = :status
           and t.name = :tagName
         order by p.hotScore desc, p.id desc
    """)
    Slice<Posts> findHotByTagName(
            @Param("status") PostStatus status,
            @Param("tagName") String tagName,
            Pageable pageable
    );

    @Query("""
        select p from Posts p
          join PostTags pt on pt.post = p
          join Tag t on pt.tag = t
         where p.status = :status
           and t.name = :tagName
           and p.id < :cursorId
         order by p.hotScore desc, p.id desc
    """)
    Slice<Posts> findHotByTagNameAfterCursor(
            @Param("status") PostStatus status,
            @Param("tagName") String tagName,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}