package CamNecT.CamNecT_Server.domain.verification.document.dto;

import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;

import java.time.LocalDateTime;

public record DocumentVerificationListItemResponse(
        Long submissionId,
        DocumentType docType,
        VerificationStatus status,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt
) {}
