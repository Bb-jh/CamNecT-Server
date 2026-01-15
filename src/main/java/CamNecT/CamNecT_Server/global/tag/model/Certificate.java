package CamNecT.CamNecT_Server.global.tag.model;

import CamNecT.CamNecT_Server.domain.users.model.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "certificate_name", nullable = false, length = 100)
    private String certificateName;

    @Column(name = "issuer_name", length = 100)
    private String issuerName;

    @Column(name = "acquired_date", nullable = false)
    private LocalDateTime acquiredDate; // 취득일

    @Column(name = "expire_date")
    private LocalDateTime expireDate; // 만료일

    @Column(name = "credential_url", length = 500)
    private String credentialUrl; // 증명 URL

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}