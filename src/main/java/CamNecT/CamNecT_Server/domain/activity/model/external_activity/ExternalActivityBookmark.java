package CamNecT.CamNecT_Server.domain.activity.model.external_activity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "external_activities_bookmark")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalActivityBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_bookmark_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Builder
    public ExternalActivityBookmark(Long userId, Long activityId) {
        this.userId = userId;
        this.activityId = activityId;
    }
}