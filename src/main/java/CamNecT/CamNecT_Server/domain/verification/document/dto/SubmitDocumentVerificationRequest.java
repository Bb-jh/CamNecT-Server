package CamNecT.CamNecT_Server.domain.verification.document.dto;

import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import jakarta.validation.constraints.*;

import java.util.List;

public record SubmitDocumentVerificationRequest(
        @NotNull DocumentType docType,
        @NotEmpty List<@NotBlank String> documentKeys
) {}
