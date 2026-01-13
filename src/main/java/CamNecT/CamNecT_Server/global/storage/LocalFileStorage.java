package CamNecT.CamNecT_Server.global.storage;

import CamNecT.CamNecT_Server.domain.verification.document.config.DocumentVerificationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.verification.document.storage", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    private final DocumentVerificationProperties props;

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    @Override
    public String save(String subDir, MultipartFile file) {
        if (!StringUtils.hasText(subDir)) throw new IllegalArgumentException("subDir 비어있음");
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("빈 파일");

        Path base = Paths.get(props.getStorageDir()).toAbsolutePath().normalize();
        Path dir = base.resolve(subDir).normalize();

        try { Files.createDirectories(dir); }
        catch (IOException e) { throw new IllegalStateException("디렉토리 생성 실패", e); }

        String contentType = file.getContentType();
        String ext = EXT_BY_CONTENT_TYPE.get(contentType);
        if (ext == null) throw new IllegalArgumentException("허용되지 않은 Content-Type: " + contentType);

        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename).normalize();

        if (!target.startsWith(base)) throw new SecurityException("허용되지 않은 경로");

        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장 실패", e);
        }

        return subDir + "/" + filename;
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        Path base = Paths.get(props.getStorageDir()).toAbsolutePath().normalize();
        Path path = base.resolve(storageKey).normalize();
        if (!path.startsWith(base)) throw new SecurityException("허용되지 않은 경로");
        return new FileSystemResource(path);
    }
}
