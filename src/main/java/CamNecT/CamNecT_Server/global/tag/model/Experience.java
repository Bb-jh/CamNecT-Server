package CamNecT.CamNecT_Server.global.tag.model;

import CamNecT.CamNecT_Server.domain.users.model.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "experience")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_id")
    private Long experienceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "major_name", length = 100)
    private String majorName; // 직무 (이미지상 major_name으로 명시됨)

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // 취업일

    @Column(name = "end_date")
    private LocalDateTime endDate; // 퇴직일

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent; // 재직중 여부

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}