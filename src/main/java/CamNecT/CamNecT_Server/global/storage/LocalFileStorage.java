package CamNecT.CamNecT_Server.global.storage;

import lombok.RequiredArgsConstructor;
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
public class LocalFileStorage implements FileStorage {

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    @Override
    public String save(String prefix, MultipartFile file) {
        if (!StringUtils.hasText(prefix)) {
            throw new IllegalArgumentException("저장 경로(prefix)가 비어있습니다.");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }
        Path dir = Paths.get(prefix).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("저장 디렉토리 생성 실패: " + e.getMessage(), e);
        }

        // ✅ 확장자는 originalFilename이 아니라 contentType 기반으로만 결정
        String contentType = file.getContentType();
        String ext = EXT_BY_CONTENT_TYPE.getOrDefault(contentType, "");

        // ✅ 파일명은 UUID만 사용 (사용자 입력 미사용)
        String filename = UUID.randomUUID() + ext;

        Path target = dir.resolve(filename).normalize();

        if (!target.startsWith(dir)) {
            throw new SecurityException("허용되지 않은 경로 접근입니다.");
        }

        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장 실패: " + e.getMessage(), e);
        }

        return target.toString();
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        return new FileSystemResource(storageKey);
    }
}
