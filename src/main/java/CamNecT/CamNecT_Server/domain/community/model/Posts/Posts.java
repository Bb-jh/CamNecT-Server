package CamNecT.CamNecT_Server.domain.community.model.Posts;

import CamNecT.CamNecT_Server.domain.community.model.Boards;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Boards board;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "context", nullable = false)
    private String content;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PostStatus status;

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
                .build();
    }

    public void update(String title, String content, Boolean isAnonymous) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (isAnonymous != null) this.isAnonymous = isAnonymous;
    }

    public void hide() { this.status = PostStatus.HIDDEN; }

    public void publish() { this.status = PostStatus.PUBLISHED; }

    public void deleteSoft() {
        this.status = PostStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
