package CamNecT.CamNecT_Server.domain.verification.document.event;

import CamNecT.CamNecT_Server.domain.verification.document.dto.ReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;

public record DocumentVerificationReviewedEvent(
        String toEmail,
        DocumentType docType,
        ReviewDocumentVerificationRequest.Decision decision,
        String reason
) {}