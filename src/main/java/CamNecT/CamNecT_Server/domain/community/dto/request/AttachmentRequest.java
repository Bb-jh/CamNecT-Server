package CamNecT.CamNecT_Server.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AttachmentRequest(
        @NotBlank @Size(max = 500) String fileUrl,
        @Size(max = 500) String thumbnailUrl,
        Integer width,
        Integer height,
        Long fileSize
) {}