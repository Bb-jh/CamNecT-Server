package CamNecT.CamNecT_Server.global.storage;

import CamNecT.CamNecT_Server.global.common.config.S3Props;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name="app.verification.document.storage", havingValue="s3")
public class S3FileStorage implements FileStorage {

    private final S3Client s3;
    private final S3Props props;

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    @Override
    public String save(String prefix, MultipartFile file) {
        if (!StringUtils.hasText(prefix)) {
            throw new IllegalArgumentException("저장 prefix가 비어있습니다.");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        String contentType = file.getContentType();
        String ext = EXT_BY_CONTENT_TYPE.get(contentType);
        if (ext == null) throw new IllegalArgumentException("허용되지 않은 Content-Type: " + contentType);

        // prefix는 "사용자 입력"이 아니라 서버가 만든 값만 넣으세요 (예: userId)
        String safePrefix = sanitizePrefix(prefix);
        String key = buildKey(safePrefix, UUID.randomUUID().toString() + ext);

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .contentType(contentType)
                .build();

        try (var in = file.getInputStream()) {
            s3.putObject(req, RequestBody.fromInputStream(in, file.getSize()));
        } catch (IOException e) {
            throw new IllegalStateException("S3 업로드 실패: " + e.getMessage(), e);
        }

        return key; // ✅ DB에는 로컬 경로가 아니라 S3 key 저장
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(props.bucket())
                .key(storageKey)
                .build();

        ResponseInputStream<GetObjectResponse> in = s3.getObject(req);
        return new InputStreamResource(in);
    }

    private String buildKey(String subPrefix, String filename) {
        String base = trimSlashes(props.prefix());
        String mid = trimSlashes(subPrefix);
        return (base + "/" + mid + "/" + filename).replaceAll("/+", "/");
    }

    private String sanitizePrefix(String prefix) {
        String p = prefix.replace("\\", "/").toLowerCase(Locale.ROOT);
        p = trimSlashes(p);
        if (p.contains("..")) throw new IllegalArgumentException("허용되지 않은 prefix 입니다.");
        return p;
    }

    private String trimSlashes(String s) {
        if (s == null) return "";
        return s.replaceAll("^/+", "").replaceAll("/+$", "");
    }
}