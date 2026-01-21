package CamNecT.CamNecT_Server.global.notification.dto;

import CamNecT.CamNecT_Server.global.notification.model.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterPushTokenRequest(
        @NotBlank String deviceId,
        @NotNull Platform platform,
        @NotBlank String token
) {}
