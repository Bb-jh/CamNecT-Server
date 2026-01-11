package CamNecT.CamNecT_Server.domain.verification.document.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document_verification_submission",
        indexes = {
                @Index(name = "idx_doc_verif_user_status", columnList = "user_id,status"),
                @Index(name = "idx_doc_verif_status_submittedAt", columnList = "status,submitted_at")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DocumentVerificationSubmission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentType docType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus status;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewer_admin_id")
    private Long reviewerAdminId;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Version
    private Long version;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentVerificationFile> files = new ArrayList<>();

    public void addFile(DocumentVerificationFile file) {
        files.add(file);
        file.setSubmission(this);
    }

    public void cancel() {
        this.status = VerificationStatus.CANCELED;
    }

    public void approve(Long adminId) {
        this.status = VerificationStatus.APPROVED;
        this.reviewerAdminId = adminId;
        this.reviewedAt = LocalDateTime.now();
        this.rejectReason = null;
    }

    public void reject(Long adminId, String reason) {
        this.status = VerificationStatus.REJECTED;
        this.reviewerAdminId = adminId;
        this.reviewedAt = LocalDateTime.now();
        this.rejectReason = reason;
    }
}
