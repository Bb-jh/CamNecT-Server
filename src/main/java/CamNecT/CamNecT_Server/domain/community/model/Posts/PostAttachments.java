package CamNecT.CamNecT_Server.domain.community.model.Posts;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_attachments",
        indexes = {
                @Index(name = "idx_post_attach_post_status_id", columnList = "post_id,status,attachment_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostAttachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "file_size")
    private Long fileSize;

    /**
     * ERD: status(active/deleted)
     * 여기서는 boolean으로:
     *  - true  = active
     *  - false = deleted
     */
    @Builder.Default
    @Column(name = "status", nullable = false)
    private boolean status = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static PostAttachments create(
            Posts post,
            String fileUrl,
            String thumbnailUrl,
            Integer width,
            Integer height,
            Long fileSize
    ) {
        return PostAttachments.builder()
                .post(post)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .width(width)
                .height(height)
                .fileSize(fileSize)
                .status(true)
                .build();
    }

    public boolean isActive() {
        return status;
    }

    public void deleteSoft() {
        this.status = false;
    }

    public String thumbnailOrFile() {
        return (thumbnailUrl != null && !thumbnailUrl.isBlank()) ? thumbnailUrl : fileUrl;
    }
}