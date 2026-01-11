package CamNecT.CamNecT_Server.domain.verification.document.dto;

import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;

import java.time.LocalDateTime;

public record SubmitDocumentVerificationResponse(
        Long submissionId,
        VerificationStatus status,
        LocalDateTime submittedAt
) {}
