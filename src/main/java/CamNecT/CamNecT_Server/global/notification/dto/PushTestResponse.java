package CamNecT.CamNecT_Server.global.notification.dto;

import java.util.List;

public record PushTestResponse(
        int requested,
        int success,
        int failure,
        int invalidTokenCount,
        List<String> invalidTokens
) {}
