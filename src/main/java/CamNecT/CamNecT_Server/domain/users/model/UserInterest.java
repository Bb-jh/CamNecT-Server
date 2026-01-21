package CamNecT.CamNecT_Server.domain.users.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_interests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_interest",
                columnNames = {"user_id", "interests_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 보장
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interests_id", nullable = false)
    private Interests interests;

    @Builder
    public UserInterest(Users user, Interests interests) {
        this.user = user;
        this.interests = interests;
    }
}