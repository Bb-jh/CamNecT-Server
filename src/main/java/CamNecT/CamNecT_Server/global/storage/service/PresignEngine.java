package CamNecT.CamNecT_Server.global.storage.service;

import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.StorageErrorCode;
import CamNecT.CamNecT_Server.global.storage.config.PresignProps;
import CamNecT.CamNecT_Server.global.storage.config.S3Props;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadRefType;
import CamNecT.CamNecT_Server.global.storage.model.UploadTicket;
import CamNecT.CamNecT_Server.global.storage.repository.UploadTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresignEngine {

    private final S3Presigner presigner;
    private final S3Client s3;
    private final S3Props s3Props;
    private final PresignProps presignProps;
    private final UploadTicketRepository ticketRepo;

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    @Transactional
    public PresignUploadResponse issueUpload(Long userId,
                                             UploadPurpose purpose,
                                             String keyPrefix,
                                             String contentType,
                                             long size,
                                             String originalFilename) {

        String ct = normalize(contentType);
        String ext = EXT_BY_CONTENT_TYPE.getOrDefault(ct, "");

        String key = buildKey(keyPrefix, UUID.randomUUID() + ext);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(presignProps.uploadExpirationSeconds());

        UploadTicket ticket = UploadTicket.builder()
                .userId(userId)
                .purpose(purpose)
                .status(UploadTicket.Status.PENDING)
                .storageKey(key)
                .originalFilename(safeName(originalFilename))
                .contentType(ct)
                .size(size)
                .expiresAt(expiresAt)
                .build();

        ticketRepo.save(ticket);

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(s3Props.bucket())
                .key(key)
                .contentType(ct)
                .build();

        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignProps.uploadExpirationSeconds()))
                .putObjectRequest(putReq)
                .build();

        String url = presigner.presignPutObject(presignReq).url().toString();

        return new PresignUploadResponse(
                key,
                url,
                expiresAt,
                Map.of("Content-Type", ct)
        );
    }

    @Transactional
    public void consume(Long userId,
                        UploadPurpose purpose,
                        UploadRefType refType,
                        Long refId,
                        String storageKey) {

        if (!StringUtils.hasText(storageKey)) {
            throw new CustomException(StorageErrorCode.STORAGE_KEY_REQUIRED);
        }

        UploadTicket t = ticketRepo.findByStorageKey(storageKey)
                .orElseThrow(() -> new CustomException(StorageErrorCode.UPLOAD_TICKET_NOT_FOUND));

        if (!Objects.equals(t.getUserId(), userId)) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_FORBIDDEN);
        }
        if (t.getPurpose() != purpose) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_FORBIDDEN);
        }
        if (!t.isUsable(LocalDateTime.now())) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_EXPIRED_OR_USED);
        }

        // ✅ 실제 S3에 올라갔는지 + size/type 일치 HEAD 검증
        HeadObjectResponse head;
        try {
            head = s3.headObject(HeadObjectRequest.builder()
                    .bucket(s3Props.bucket())
                    .key(storageKey)
                    .build());
        } catch (NoSuchKeyException e) {
            throw new CustomException(StorageErrorCode.STORAGE_NOT_FOUND);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) throw new CustomException(StorageErrorCode.STORAGE_NOT_FOUND);
            throw new CustomException(StorageErrorCode.STORAGE_DOWNLOAD_FAILED, e);
        }

        if (head.contentLength() != null && !head.contentLength().equals(t.getSize())) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_MISMATCHED_OBJECT);
        }
        if (StringUtils.hasText(head.contentType())
                && !normalize(head.contentType()).equals(normalize(t.getContentType()))) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_MISMATCHED_OBJECT);
        }

        t.markUsed(refType, refId);
    }

    private String buildKey(String mid, String filename) {
        String base = trimSlashes(s3Props.prefix());
        String m = trimSlashes(mid);
        return (base + "/" + m + "/" + filename).replaceAll("/+", "/");
    }

    private String trimSlashes(String s) {
        if (s == null) return "";
        return s.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private String safeName(String name) {
        return StringUtils.hasText(name) ? name : "file";
    }

    private String normalize(String ct) {
        return (ct == null) ? "" : ct.trim().toLowerCase(Locale.ROOT);
    }
}
