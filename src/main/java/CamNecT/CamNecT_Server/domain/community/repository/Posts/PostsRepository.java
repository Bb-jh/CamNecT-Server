package CamNecT.CamNecT_Server.domain.community.repository.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Posts.Posts;
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
    Slice<Posts> findByStatusOrderByIdDesc(PostStatus status, Pageable pageable);
    Slice<Posts> findByStatusAndIdLessThanOrderByIdDesc(PostStatus status, Long cursorId, Pageable pageable);

    Slice<Posts> findByStatusAndBoard_CodeOrderByIdDesc(PostStatus status, BoardCode code, Pageable pageable);
    Slice<Posts> findByStatusAndBoard_CodeAndIdLessThanOrderByIdDesc(PostStatus status, BoardCode code, Long cursorId, Pageable pageable);

    // =========================
    // Featured (상단 카드)
    // =========================
    List<Posts> findTop5ByStatusOrderByIdDesc(PostStatus status);
    List<Posts> findTop5ByStatusAndBoard_CodeOrderByIdDesc(PostStatus status, BoardCode code);


    // =========================
    // Waiting Questions (답변대기)
    // PostStats.commentCount == 0
    // =========================
    @Query("""
        select p
        from Posts p
        join PostStats ps on ps.post = p
        where p.status = :status
          and p.board.code = :code
          and ps.commentCount = 0
        order by p.id desc
    """)
    List<Posts> findTop3Waiting(
            @Param("status") PostStatus status,
            @Param("code") BoardCode code,
            Pageable pageable   // PageRequest.of(0,3)로 제한
    );

    // =========================
    // Popular/Hot (인기글/추천글)
    // PostStats.hotScore 기준
    // =========================
    @Query("""
        select p
        from Posts p
        join PostStats ps on ps.post = p
        where p.status = :status
        order by ps.hotScore desc, p.id desc
    """)
    List<Posts> findTopHot(
            @Param("status") PostStatus status,
            Pageable pageable   // PageRequest.of(0,5)
    );

    @Query("""
        select p
        from Posts p
        join PostStats ps on ps.post = p
        where p.status = :status
          and p.board.code = :code
        order by ps.hotScore desc, p.id desc
    """)
    List<Posts> findTopHotByBoard(
            @Param("status") PostStatus status,
            @Param("code") BoardCode code,
            Pageable pageable
    );

    // =========================
    // Tag-based recommendation
    // PostStats.hotScore 기준
    // =========================
    @Query("""
        select p
        from Posts p
        join PostStats ps on ps.post = p
        join PostTags pt on pt.post = p
        join pt.tag t
        where p.status = :status
          and t.name = :tagName
        order by ps.hotScore desc, p.id desc
    """)
    Slice<Posts> findHotByTagName(
            @Param("status") PostStatus status,
            @Param("tagName") String tagName,
            Pageable pageable
    );

    @Query("""
        select p
        from Posts p
        join PostStats ps on ps.post = p
        join PostTags pt on pt.post = p
        join pt.tag t
        where p.status = :status
          and t.name = :tagName
          and p.id < :cursorId
        order by ps.hotScore desc, p.id desc
    """)
    Slice<Posts> findHotByTagNameAfterCursor(
            @Param("status") PostStatus status,
            @Param("tagName") String tagName,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}