package CamNecT.CamNecT_Server.domain.portfolio.model;

import CamNecT.CamNecT_Server.global.common.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PortfolioProject")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
public class PortfolioProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "thumbnail_url", length = 500)
    @Builder.Default
    private String thumbnailUrl = "기본이미지";

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private boolean isFavorite = false;

    @Column(name = "project_field", nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> projectField = new ArrayList<>();

    @Column(name = "assigned_role", nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> assignedRole = new ArrayList<>();

    @Column(name = "tech_stack", nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> techStack = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt; // 생성일 (DATE 타입)

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt; // 수정일 (DATE 타입)

    @OneToMany(mappedBy = "portfolioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioAsset> assets = new ArrayList<>();

    public void updateThumbnail(String url) {
        this.thumbnailUrl = url;
    }

    public void updateInfo(String title, String description, String review, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.review = review;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedAt = LocalDate.now();
    }
}