package CamNecT.CamNecT_Server.domain.verification.document.service;

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
import CamNecT.CamNecT_Server.global.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentVerificationService {

    private final DocumentVerificationProperties props;
    private final FileStorage fileStorage;
    private final DocumentVerificationSubmissionRepository submissionRepo;

    @Transactional
    public SubmitDocumentVerificationResponse submit(Long userId, DocumentType docType, List<MultipartFile> documents) {
        if (documents == null || documents.isEmpty()) {
            throw new CustomException(VerificationErrorCode.DOCUMENTS_REQUIRED);

        }
        if (documents.size() > props.getMaxFiles()) {
            throw new CustomException(VerificationErrorCode.TOO_MANY_FILES);
        }

        // 유저당 PENDING 1개 제한(추천 정책)
        if (submissionRepo.existsByUserIdAndStatus(userId, VerificationStatus.PENDING)) {
            // throw new CustomException(ErrorCode.VERIFICATION_PENDING_EXISTS);
            throw new CustomException(VerificationErrorCode.PENDING_ALREADY_EXISTS);
        }

        DocumentVerificationSubmission sub = DocumentVerificationSubmission.builder()
                .userId(userId)
                .docType(docType)
                .status(VerificationStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build();

        submissionRepo.save(sub);

        String prefix = "user-" + userId + "/submission-" + sub.getId();

        for (MultipartFile file : documents) {
            validateFile(file);

            String storageKey = fileStorage.save(prefix, file); // 여기만 이렇게 바꾸면 됨

            DocumentVerificationFile vf = DocumentVerificationFile.builder()
                    .originalFilename(safeName(file.getOriginalFilename()))
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .storageKey(storageKey) // "verifications/user-.../submission-.../uuid.pdf"
                    .uploadedAt(LocalDateTime.now())
                    .build();

            sub.addFile(vf);
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

    @Transactional
    public void cancel(Long userId, Long submissionId) {
        DocumentVerificationSubmission r = submissionRepo.findByIdAndUserId(submissionId, userId)
                .orElseThrow(() -> new CustomException(VerificationErrorCode.SUBMISSION_NOT_FOUND));

        if (r.getStatus() != VerificationStatus.PENDING) {
            throw new CustomException(VerificationErrorCode.ONLY_PENDING_CAN_REVIEW);
        }

        r.cancel();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(VerificationErrorCode.EMPTY_FILE_NOT_ALLOWED);
        }
        if (file.getSize() > props.maxFileSizeBytes()) {
            throw new CustomException(VerificationErrorCode.FILE_TOO_LARGE);
        }
        String ct = file.getContentType();
        if (!StringUtils.hasText(ct) || !props.getAllowedContentTypes().contains(ct)) {
            throw new CustomException(VerificationErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }
    }

    private String safeName(String name) {
        return StringUtils.hasText(name) ? name : "file";
    }
}
