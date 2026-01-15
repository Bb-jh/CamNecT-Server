package CamNecT.CamNecT_Server.domain.users.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_tag_map")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserTagMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long userTagId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 태그의 경우 상세 정보가 필요한 경우가 많으므로 연관 관계를 유지하거나,
    // 이 또한 id만 남길 수 있습니다. 여기서는 id만 남기는 방식으로 통일합니다.
    @Column(name = "tag_id", nullable = false)
    private Long tagId;
}