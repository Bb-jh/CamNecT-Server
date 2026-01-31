package CamNecT.CamNecT_Server.domain.verification.document.service;

import CamNecT.CamNecT_Server.domain.users.model.UserRole;
import CamNecT.CamNecT_Server.domain.users.model.UserStatus;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.users.repository.UserRepository;
import CamNecT.CamNecT_Server.domain.verification.document.event.DocumentVerificationReviewedEvent;
import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationFileRepository;
import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationSubmissionRepository;
import CamNecT.CamNecT_Server.domain.verification.document.dto.ReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationFile;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationSubmission;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.UserErrorCode;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.VerificationErrorCode;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignDownloadResponse;
import CamNecT.CamNecT_Server.global.storage.service.PresignEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDocumentVerificationService {

    private final DocumentVerificationSubmissionRepository submissionRepo;
    private final DocumentVerificationFileRepository fileRepo;
    private final UserRepository usersRepository;
    private final PresignEngine presignEngine;
    private final ApplicationEventPublisher eventPublisher;

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

        if(!usersRepository.existsByUserIdAndRole(adminId, UserRole.ADMIN)){
            throw new CustomException(UserErrorCode.USER_NOT_ADMIN);
        }

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

            eventPublisher.publishEvent(new DocumentVerificationReviewedEvent(
                    user.getEmail(), s.getDocType(), req.decision(), null
            ));
            return;
        }

        String reason = (req.reason() == null) ? "" : req.reason().trim();
        if (reason.isBlank()) {
            throw new CustomException(VerificationErrorCode.REJECT_REASON_REQUIRED);
        }
        s.reject(adminId, reason);

        eventPublisher.publishEvent(new DocumentVerificationReviewedEvent(
                user.getEmail(), s.getDocType(), req.decision(), reason
        ));
    }

    @Transactional(readOnly = true)
    public PresignDownloadResponse downloadUrl(Long adminId, Long submissionId, Long fileId) {
        if(!usersRepository.existsByUserIdAndRole(adminId, UserRole.ADMIN)){
            throw new CustomException(UserErrorCode.USER_NOT_ADMIN);
        }

        DocumentVerificationFile f = fileRepo.findByIdAndSubmission_Id(fileId, submissionId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.FILE_NOT_FOUND));

        String ct = (f.getContentType() == null) ? "" : f.getContentType();
        String name = (f.getOriginalFilename() == null || f.getOriginalFilename().isBlank())
                ? "document" : f.getOriginalFilename();

        return presignEngine.presignDownload(f.getStorageKey(), name, ct);
    }
}
