package CamNecT.CamNecT_Server.global.notification.dto;

public record RegisterPushTokenResponse(
        Long pushDeviceId,
        boolean created
) {}