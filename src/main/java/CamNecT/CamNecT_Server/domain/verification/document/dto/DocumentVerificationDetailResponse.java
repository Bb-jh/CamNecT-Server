package CamNecT.CamNecT_Server.domain.verification.document.dto;

import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;

import java.time.LocalDateTime;

public record DocumentVerificationDetailResponse(
        Long submissionId,
        DocumentType docType,
        VerificationStatus status,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        String rejectReason,
        DocumentVerificationFileDto file
) {}
