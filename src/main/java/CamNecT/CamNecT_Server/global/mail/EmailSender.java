package CamNecT.CamNecT_Server.global.mail;

import CamNecT.CamNecT_Server.domain.verification.document.dto.AdminReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;

public interface EmailSender {
    void sendEmailVerificationCode(String toEmail, String code, long expiresMinutes);

    void sendDocumentVerificationResult(String toEmail,
                                        DocumentType docType,
                                        AdminReviewDocumentVerificationRequest.Decision decision,
                                        String reason);
}
