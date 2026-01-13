package CamNecT.CamNecT_Server.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    /**
     * 타입: department / topic / custom
     */
    @Column(length = 20, nullable = false)
    private String type = "department"; // 기본값 설정

    @Column(length = 50)
    private String code;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 기본값 TRUE

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public Tags(String name, String type, String code, Boolean isActive) {
        this.name = name;
        this.type = (type != null) ? type : "department";
        this.code = code;
        this.isActive = (isActive != null) ? isActive : true;
    }
}