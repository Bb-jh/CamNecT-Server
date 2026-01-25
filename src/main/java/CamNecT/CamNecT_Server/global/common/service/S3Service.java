package CamNecT.CamNecT_Server.global.common.service;

import CamNecT.CamNecT_Server.global.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

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

    public void deleteFileByKey(String key) {
        fileStorage.delete(key);
    }
}