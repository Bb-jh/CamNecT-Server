package CamNecT.CamNecT_Server.global.mail;

import CamNecT.CamNecT_Server.domain.verification.document.dto.ReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;

public interface EmailSender {
    void sendEmailVerificationCode(String toEmail, String code, long expiresMinutes);

    void sendDocumentVerificationResult(String toEmail,
                                        DocumentType docType,
                                        ReviewDocumentVerificationRequest.Decision decision,
                                        String reason);
}
