package CamNecT.CamNecT_Server.domain.verification.document.dto;

import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;

import java.time.LocalDateTime;

public record AdminDocumentVerificationListItemResponse(
        Long submissionId,
        VerificationStatus status,
        DocumentType docType,
        LocalDateTime submittedAt,
        Long userId,
        String username,
        String phoneNum
) {}