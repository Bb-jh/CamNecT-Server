package CamNecT.CamNecT_Server.domain.verification.document.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewDocumentVerificationRequest(
        @NotNull Decision decision,
        String reason
) {
    public enum Decision { APPROVE, REJECT }
}
