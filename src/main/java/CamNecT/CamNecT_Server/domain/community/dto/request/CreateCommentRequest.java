package CamNecT.CamNecT_Server.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank String content,
        Long parentCommentId
) {}

