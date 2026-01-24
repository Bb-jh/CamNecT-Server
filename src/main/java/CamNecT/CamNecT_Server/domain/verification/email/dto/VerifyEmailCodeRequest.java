package CamNecT.CamNecT_Server.domain.verification.email.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailCodeRequest(
        @NotNull Long userId,
        @Pattern(regexp = "\\d{6}") String code
) {}
