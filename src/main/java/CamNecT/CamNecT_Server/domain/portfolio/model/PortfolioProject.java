package CamNecT.CamNecT_Server.domain.portfolio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PortfolioProject")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String thumbnailUrl = "기본이미지"; //기본이미지 추가 필요

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "sort_order")
    private Integer sortOrder;

    // 양방향 연관관계 설정
    @OneToMany(mappedBy = "portfolioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioAsset> assets = new ArrayList<>();
}