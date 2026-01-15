package CamNecT.CamNecT_Server.domain.users.model;

import CamNecT.CamNecT_Server.global.tag.model.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interest_tag_map")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestTagMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 보통 매핑 테이블도 대리 키(ID)를 두는 것이 관리상 편리합니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interests_id", nullable = false)
    private Interests interest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Builder
    public InterestTagMap(Interests interest, Tag tag) {
        this.interest = interest;
        this.tag = tag;
    }
}