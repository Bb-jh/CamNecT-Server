package CamNecT.CamNecT_Server.global.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final FileStorage fileStorage;

    // 기존 시그니처 유지하고 싶으면 prefix를 고정하거나(예: "misc")
    public String uploadFile(MultipartFile file) {
        // 예: 기존 호출부가 prefix를 모른다면 일단 "misc"로 수렴
        return fileStorage.save("misc", file); // key 리턴
    }

    // 신규: 도메인 prefix를 받을 수 있게 확장
    public String uploadFile(String prefix, MultipartFile file) {
        return fileStorage.save(prefix, file); // key 리턴
    }

    public void deleteFile(String key) {
        fileStorage.delete(key);
    }
}