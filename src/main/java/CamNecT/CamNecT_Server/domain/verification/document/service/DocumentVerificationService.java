package CamNecT.CamNecT_Server.domain.verification.document.service;

import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationFileRepository;
import CamNecT.CamNecT_Server.domain.verification.document.repository.DocumentVerificationSubmissionRepository;
import CamNecT.CamNecT_Server.domain.verification.document.config.DocumentVerificationProperties;
import CamNecT.CamNecT_Server.domain.verification.document.dto.DocumentVerificationDetailResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.DocumentVerificationFileDto;
import CamNecT.CamNecT_Server.domain.verification.document.dto.DocumentVerificationListItemResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.SubmitDocumentVerificationResponse;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationFile;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationSubmission;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.VerificationErrorCode;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignDownloadResponse;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadRefType;
import CamNecT.CamNecT_Server.global.storage.model.UploadTicket;
import CamNecT.CamNecT_Server.global.storage.repository.UploadTicketRepository;
import CamNecT.CamNecT_Server.global.storage.service.FileStorage;
import CamNecT.CamNecT_Server.global.storage.service.PresignEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentVerificationService {

    private final DocumentVerificationProperties props;

    private final DocumentVerificationSubmissionRepository submissionRepo;
    private final DocumentVerificationFileRepository fileRepo;

    private final PresignEngine presignEngine;
    private final UploadTicketRepository ticketRepo;
    private final FileStorage fileStorage;

    @Transactional
    public PresignUploadResponse presignUpload(Long userId, PresignUploadRequest req) {
        String ct = normalize(req.contentType());

        if (req.size() <= 0) {
            throw new CustomException(VerificationErrorCode.EMPTY_FILE_NOT_ALLOWED);
        }
        if (req.size() > props.maxFileSizeBytes()) {
            throw new CustomException(VerificationErrorCode.FILE_TOO_LARGE);
        }
        if (!StringUtils.hasText(ct) || !props.getAllowedContentTypes().contains(ct)) {
            throw new CustomException(VerificationErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }

        // 유저당 3개 제한(발급 단계에서 1차 방어)
        long pending = ticketRepo.countByUserIdAndPurposeAndStatus(
                userId, UploadPurpose.VERIFICATION_DOCUMENT, UploadTicket.Status.PENDING
        );
        if (pending >= props.getMaxFiles()) {
            throw new CustomException(VerificationErrorCode.TOO_MANY_FILES);
        }

        // prefix는 submissionId 없이도 충분히 안전 (원하면 "verification/user-..../temp"로 유지)
        String keyPrefix = "verification/user-" + userId + "/documents";

        return presignEngine.issueUpload(
                userId,
                UploadPurpose.VERIFICATION_DOCUMENT,
                keyPrefix,
                ct,
                req.size(),
                req.originalFilename()
        );
    }

    @Transactional
    public SubmitDocumentVerificationResponse submit(Long userId, DocumentType docType, List<String> documentKeys) {

        if (documentKeys == null || documentKeys.isEmpty()) {
            throw new CustomException(VerificationErrorCode.DOCUMENTS_REQUIRED);
        }
        if (documentKeys.size() > props.getMaxFiles()) {
            throw new CustomException(VerificationErrorCode.TOO_MANY_FILES);
        }

        if (submissionRepo.existsByUserIdAndStatus(userId, VerificationStatus.PENDING)) {
            throw new CustomException(VerificationErrorCode.PENDING_ALREADY_EXISTS);
        }

        DocumentVerificationSubmission sub = DocumentVerificationSubmission.builder()
                .userId(userId)
                .docType(docType)
                .status(VerificationStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build();

        submissionRepo.save(sub);

        int savedCount = 0;

        for (String key : documentKeys) {
            if (!StringUtils.hasText(key)) continue;

            // ✅ 티켓 소유/만료/목적 + S3 HEAD 검증까지 여기서 처리됨
            presignEngine.consume(
                    userId,
                    UploadPurpose.VERIFICATION_DOCUMENT,
                    UploadRefType.VERIFICATION,
                    sub.getId(),
                    key
            );

            UploadTicket t = ticketRepo.findByStorageKey(key)
                    .orElseThrow(() -> new CustomException(VerificationErrorCode.FILE_NOT_FOUND));

            DocumentVerificationFile vf = DocumentVerificationFile.builder()
                    .originalFilename(safeName(t.getOriginalFilename()))
                    .contentType(t.getContentType())
                    .size(t.getSize())
                    .storageKey(key)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            sub.addFile(vf);

            if (++savedCount >= props.getMaxFiles()) break;
        }

        DocumentVerificationSubmission saved = submissionRepo.save(sub);
        return new SubmitDocumentVerificationResponse(saved.getId(), saved.getStatus(), saved.getSubmittedAt());
    }

    @Transactional(readOnly = true)
    public List<DocumentVerificationListItemResponse> mySubmissions(Long userId) {
        return submissionRepo.findByUserIdOrderBySubmittedAtDesc(userId).stream()
                .map(r -> new DocumentVerificationListItemResponse(
                        r.getId(), r.getDocType(), r.getStatus(), r.getSubmittedAt(), r.getReviewedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentVerificationDetailResponse mySubmissionDetail(Long userId, Long submissionId) {
        DocumentVerificationSubmission r = submissionRepo.findByIdAndUserId(submissionId, userId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.SUBMISSION_NOT_FOUND));

        var files = r.getFiles().stream()
                .map(f -> new DocumentVerificationFileDto(f.getId(), f.getOriginalFilename(), f.getContentType(), f.getSize()))
                .toList();

        return new DocumentVerificationDetailResponse(
                r.getId(), r.getDocType(), r.getStatus(), r.getSubmittedAt(), r.getReviewedAt(), r.getRejectReason(), files
        );
    }

    @Transactional(readOnly = true)
    public PresignDownloadResponse myFileDownloadUrl(Long userId, Long submissionId, Long fileId) {

        // 소유 검증 (submission)
        submissionRepo.findByIdAndUserId(submissionId, userId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.SUBMISSION_NOT_FOUND));

        DocumentVerificationFile f = fileRepo.findByIdAndSubmission_Id(fileId, submissionId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.FILE_NOT_FOUND));

        String filename = safeName(f.getOriginalFilename());
        String ct = normalize(f.getContentType());

        return presignEngine.presignDownload(f.getStorageKey(), filename, ct);
    }

    @Transactional
    public void cancel(Long userId, Long submissionId) {
        DocumentVerificationSubmission r = submissionRepo.findByIdAndUserId(submissionId, userId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.SUBMISSION_NOT_FOUND));

        if (r.getStatus() != VerificationStatus.PENDING) {
            throw new CustomException(VerificationErrorCode.ONLY_PENDING_CAN_REVIEW);
        }

        Set<String> keys = new HashSet<>();
        r.getFiles().forEach(f -> {
            if (StringUtils.hasText(f.getStorageKey())) keys.add(f.getStorageKey());
        });

        r.cancel();

        registerAfterCommitDelete(keys);
    }

    private void registerAfterCommitDelete(Set<String> keys) {
        if (keys == null || keys.isEmpty()) return;

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (String key : keys) {
                        try { fileStorage.delete(key); } catch (Exception ignored) {}
                    }
                }
            });
        } else {
            for (String key : keys) {
                try { fileStorage.delete(key); } catch (Exception ignored) {}
            }
        }
    }

    private String safeName(String name) {
        return StringUtils.hasText(name) ? name : "file";
    }

    private String normalize(String ct) {
        return (ct == null) ? "" : ct.trim().toLowerCase(Locale.ROOT);
    }
}