package CamNecT.CamNecT_Server.domain.users.model;

import CamNecT.CamNecT_Server.domain.model.Institutions;
import CamNecT.CamNecT_Server.domain.model.Majors;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserProfile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId // Users 엔티티의 PK를 그대로 UserProfile의 PK로 사용 (1:1 관계)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "open_to_coffeechat", nullable = false)
    @Builder.Default
    private Boolean openToCoffeeChat = false;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "student_no", nullable = false, length = 20)
    private String studentNo;

    @Column(name = "year_level", nullable = false)
    private Integer yearLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institutions institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", nullable = false)
    private Majors major;
}