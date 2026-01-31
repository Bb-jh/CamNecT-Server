package CamNecT.CamNecT_Server.domain.verification.document.dto;

import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import jakarta.validation.constraints.*;

public record SubmitDocumentVerificationRequest(
        @NotNull DocumentType docType,
        @NotEmpty String documentKey
) {}
