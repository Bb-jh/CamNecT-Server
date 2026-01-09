package CamNecT.CamNecT_Server.domain.auth.dto.login;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
