package CamNecT.CamNecT_Server.domain.activity.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "external_activities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityCategory category;

    @Column(length = 100)
    private String organizer;

    @Column(length = 50)
    private String region;

    @Column(length = 300)
    private String targetDescription;

    @Column(nullable = false)
    private LocalDate applyStartDate;

    @Column(nullable = false)
    private LocalDate applyEndDate;

    private LocalDate resultAnnounceDate;

    @Column(length = 500)
    private String officialUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActivityStatus status = ActivityStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String context;
}