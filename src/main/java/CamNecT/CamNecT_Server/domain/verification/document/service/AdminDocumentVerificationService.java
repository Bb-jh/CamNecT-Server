package CamNecT.CamNecT_Server.domain.verification.document.service;

import CamNecT.CamNecT_Server.domain.users.model.UserStatus;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.users.repository.UserRepository;
import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationFileRepository;
import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationSubmissionRepository;
import CamNecT.CamNecT_Server.domain.verification.document.dto.DownloadResult;
import CamNecT.CamNecT_Server.domain.verification.document.dto.ReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationFile;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationSubmission;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.VerificationErrorCode;
import CamNecT.CamNecT_Server.global.storage.service.FileStorage;
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
    private final UserRepository usersRepository;

    @Transactional(readOnly = true)
    public Page<DocumentVerificationSubmission> list(VerificationStatus status, Pageable pageable) {
        return submissionRepo.findByStatusOrderBySubmittedAtDesc(status, pageable);
    }

    @Transactional(readOnly = true)
    public DocumentVerificationSubmission get(Long submissionId) {
        return submissionRepo.findById(submissionId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.SUBMISSION_NOT_FOUND));
    }

    @Transactional
    public void review(Long adminId, Long submissionId, ReviewDocumentVerificationRequest req) {
        DocumentVerificationSubmission s = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.SUBMISSION_NOT_FOUND));

        if (s.getStatus() != VerificationStatus.PENDING) {
            throw new CustomException(VerificationErrorCode.ONLY_PENDING_CAN_REVIEW);
        }

        Users user = usersRepository.findById(s.getUserId())
                .orElseThrow(() -> new CustomException(VerificationErrorCode.USER_NOT_FOUND));

        if (req.decision() == ReviewDocumentVerificationRequest.Decision.APPROVE) {
            s.approve(adminId);
            if (user.getStatus() != UserStatus.SUSPENDED) {
                user.changeStatus(UserStatus.ACTIVE);
            }
        }

        // REJECT
        String reason = (req.reason() == null) ? "" : req.reason().trim();
        if (reason.isBlank()) {
            throw new CustomException(VerificationErrorCode.REJECT_REASON_REQUIRED);
        }
        s.reject(adminId, reason);
    }

    @Transactional(readOnly = true)
    public DownloadResult downloadFileWithMeta(Long submissionId, Long fileId) {
        DocumentVerificationFile f = fileRepo.findByIdAndSubmission_Id(fileId, submissionId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.FILE_NOT_FOUND));

        Resource resource = fileStorage.loadAsResource(f.getStorageKey());

        String ct = (f.getContentType() == null || f.getContentType().isBlank())
                ? "application/octet-stream" : f.getContentType();

        String name = (f.getOriginalFilename() == null || f.getOriginalFilename().isBlank())
                ? "document" : f.getOriginalFilename();

        return new DownloadResult(resource, name, ct, f.getSize());
    }
}


