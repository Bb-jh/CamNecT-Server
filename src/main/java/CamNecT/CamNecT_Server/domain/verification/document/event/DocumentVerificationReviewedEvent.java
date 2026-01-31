package CamNecT.CamNecT_Server.domain.verification.document.event;

import CamNecT.CamNecT_Server.domain.verification.document.dto.AdminReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;

public record DocumentVerificationReviewedEvent(
        String toEmail,
        DocumentType docType,
        AdminReviewDocumentVerificationRequest.Decision decision,
        String reason
) {}