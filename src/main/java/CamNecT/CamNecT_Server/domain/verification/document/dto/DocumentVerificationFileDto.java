package CamNecT.CamNecT_Server.domain.verification.document.dto;

public record DocumentVerificationFileDto(
        Long fileId,
        String originalFilename,
        String contentType,
        long size
) {}