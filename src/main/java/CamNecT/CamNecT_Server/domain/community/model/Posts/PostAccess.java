package CamNecT.CamNecT_Server.domain.community.model.Posts;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_access",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_access_user_post",
                columnNames = {"user_id", "post_id"}
        ),
        indexes = {
                @Index(name = "idx_post_access_user", columnList = "user_id"),
                @Index(name = "idx_post_access_post", columnList = "post_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_access_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    @Column(name = "paid_points", nullable = false)
    private int paidPoints;

    @CreationTimestamp
    @Column(name = "purchased_at", nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    public static PostAccess of(Long userId, Posts post, int paidPoints) {
        PostAccess pa = new PostAccess();
        pa.userId = userId;
        pa.post = post;
        pa.paidPoints = paidPoints;
        return pa;
    }
}
