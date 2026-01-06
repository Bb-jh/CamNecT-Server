package CamNecT.CamNecT_Server.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Colleges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Colleges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "college_id")
    private Long collegeId;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 적용
    @JoinColumn(name = "institution_id", nullable = false)
    private Institutions institution;

    @Column(name = "college_code", nullable = false, length = 100)
    private String collegeCode;

    @Column(name = "college_name_kor", nullable = false, length = 100)
    private String collegeNameKor;

    @Column(name = "college_name_eng", nullable = false, length = 100)
    private String collegeNameEng;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}