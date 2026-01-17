package CamNecT.CamNecT_Server.domain.verification.document.repository;

import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentVerificationFileRepository extends JpaRepository<DocumentVerificationFile, Long> {
    Optional<DocumentVerificationFile> findByIdAndSubmission_Id(Long fileId, Long submissionId);
}
