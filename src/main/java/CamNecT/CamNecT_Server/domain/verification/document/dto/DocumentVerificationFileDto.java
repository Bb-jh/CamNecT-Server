package CamNecT.CamNecT_Server.domain.verification.document.dto;

public record DocumentVerificationFileDto(
        String originalFilename,
        String contentType,
        long size,
        String storageKey
) {}