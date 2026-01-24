package CamNecT.CamNecT_Server.domain.users.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_interest_tag_map",
        indexes = @Index(name="idx_user_interest_tag_user", columnList = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserInterestTagMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_interest_tag_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    // 추천 우선순위 신호 주고 싶으면 이거까지 권장(선택)
    // @Column(name="weight", nullable=false)
    // private int weight; // 기본 1
}
