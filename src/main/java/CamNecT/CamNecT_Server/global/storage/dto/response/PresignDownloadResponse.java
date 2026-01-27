package CamNecT.CamNecT_Server.global.storage.dto.response;

import java.time.LocalDateTime;

public record PresignDownloadResponse(
        String downloadUrl,
        LocalDateTime expiresAt,
        String fileKey
) {}