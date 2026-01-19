package CamNecT.CamNecT_Server.domain.verification.document.service;

import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationFileRepository;
import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationSubmissionRepository;
import CamNecT.CamNecT_Server.domain.verification.document.dto.DownloadResult;
import CamNecT.CamNecT_Server.domain.verification.document.dto.ReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationFile;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationSubmission;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;
import CamNecT.CamNecT_Server.global.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDocumentVerificationService {

    private final DocumentVerificationSubmissionRepository submissionRepo;
    private final DocumentVerificationFileRepository fileRepo;
    private final FileStorage fileStorage;

    @Transactional(readOnly = true)
    public Page<DocumentVerificationSubmission> list(VerificationStatus status, Pageable pageable) {
        return submissionRepo.findByStatusOrderBySubmittedAtDesc(status, pageable);
    }

    @Transactional(readOnly = true)
    public DocumentVerificationSubmission get(Long submissionId) {
        return submissionRepo.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
    }

    @Transactional
    public void review(Long adminId, Long submissionId, ReviewDocumentVerificationRequest req) {
        DocumentVerificationSubmission s = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (s.getStatus() != VerificationStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태만 처리할 수 있습니다.");
        }

        if (req.decision() == ReviewDocumentVerificationRequest.Decision.APPROVE) {
            s.approve(adminId);
            return;
        }

        // REJECT
        String reason = (req.reason() == null) ? "" : req.reason().trim();
        if (reason.isBlank()) {
            throw new IllegalArgumentException("반려 사유가 필요합니다.");
        }
        s.reject(adminId, reason);
    }

    @Transactional(readOnly = true)
    public DownloadResult downloadFileWithMeta(Long submissionId, Long fileId) {
        DocumentVerificationFile f = fileRepo.findByIdAndSubmission_Id(fileId, submissionId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        Resource resource = fileStorage.loadAsResource(f.getStorageKey());

        String ct = (f.getContentType() == null || f.getContentType().isBlank())
                ? "application/octet-stream" : f.getContentType();

        String name = (f.getOriginalFilename() == null || f.getOriginalFilename().isBlank())
                ? "document" : f.getOriginalFilename();

        return new DownloadResult(resource, name, ct, f.getSize());
    }
}


