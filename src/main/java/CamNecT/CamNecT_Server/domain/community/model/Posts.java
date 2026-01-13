package CamNecT.CamNecT_Server.domain.community.model;

import CamNecT.CamNecT_Server.domain.community.model.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    // board_id (FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Boards board;

    // user_id (지금은 User 도메인 없으면 Long으로 두는 것도 OK)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // varchar(200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // text (ERD가 context라면 그대로 매핑)
    @Lob
    @Column(name = "context", nullable = false)
    private String content;

    // tinyint(1) default 0
    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    // varchar(20)
    @Column(name = "status", nullable = false, length = 20)
    private PostStatus status;

    @Column(name = "hot_score", nullable = false)
    private long hotScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static Posts create(Boards board, Long userId, String title, String content, boolean isAnonymous) {
        return Posts.builder()
                .board(board)
                .userId(userId)
                .title(title)
                .content(content)
                .isAnonymous(isAnonymous)
                .status(PostStatus.PUBLISHED)
                .hotScore(0L)
                .build();
    }

    public void hide() {
        this.status = PostStatus.HIDDEN;
    }

    public void publish() {
        this.status = PostStatus.PUBLISHED;
    }

    public void deleteSoft() {
        this.status = PostStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
