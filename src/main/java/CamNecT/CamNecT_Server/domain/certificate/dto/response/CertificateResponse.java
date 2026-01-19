package CamNecT.CamNecT_Server.domain.certificate.dto.response;

import CamNecT.CamNecT_Server.domain.certificate.model.Certificate;

import java.time.LocalDate;

public record CertificateResponse(
        Long certificateId,
        String certificateName,
        String issuerName,
        LocalDate acquiredDate,
        LocalDate expireDate,
        String credentialUrl,
        String description
) {
    public static CertificateResponse from(Certificate certificate) {
        return new CertificateResponse(
                certificate.getCertificateId(),
                certificate.getCertificateName(),
                certificate.getIssuerName(),
                certificate.getAcquiredDate(),
                certificate.getExpireDate(),
                certificate.getCredentialUrl(),
                certificate.getDescription()
        );
    }
}
