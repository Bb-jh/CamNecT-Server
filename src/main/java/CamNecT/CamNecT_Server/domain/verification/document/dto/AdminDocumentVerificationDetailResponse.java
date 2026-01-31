package CamNecT.CamNecT_Server.domain.verification.document.dto;

import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;

import java.time.LocalDateTime;

public record AdminDocumentVerificationDetailResponse(
        Long submissionId,
        VerificationStatus status,
        DocumentType docType,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        String rejectReason,

        Long userId,
        String username,
        String phoneNum,
        String name,

        String studentNo,
        Integer yearLevel,
        Long institutionId,
        Long majorId,

        String originalFilename,
        String contentType,
        long size
) {}