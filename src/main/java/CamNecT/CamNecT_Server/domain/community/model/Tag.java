package CamNecT.CamNecT_Server.domain.community.model;

import CamNecT.CamNecT_Server.domain.community.model.enums.TagType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tags",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tags_type_name",
                columnNames = {"type", "name"}
        ),
        indexes = {
                @Index(name = "idx_tags_type_name", columnList = "type,name")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TagType type; // MAJOR / INTEREST

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Tag of(TagType type, String name) {
        return Tag.builder()
                .type(type)
                .name(name)
                .build();
    }
}