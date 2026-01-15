package CamNecT.CamNecT_Server.domain.users.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interests_id")
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code; // 개발자용 식별 코드

    @Column(name = "name_kor", length = 100, nullable = false)
    private String nameKor;

    @Column(name = "name_en", length = 100, nullable = false)
    private String nameEn;

    @Column(name = "category", length = 30, nullable = false)
    private String category;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public Interests(String code, String nameKor, String nameEn, String category, Boolean isActive) {
        this.code = code;
        this.nameKor = nameKor;
        this.nameEn = nameEn;
        this.category = category;
        this.isActive = isActive;
    }
}