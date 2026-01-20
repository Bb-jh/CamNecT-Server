package CamNecT.CamNecT_Server.domain.certificate.dto.request;

import java.time.LocalDate;

public record CertificateRequest(
        Long userId,
        String certificateName,
        String issuerName,
        LocalDate acquiredDate,
        LocalDate expireDate,
        String credentialUrl,
        String description
) {
}