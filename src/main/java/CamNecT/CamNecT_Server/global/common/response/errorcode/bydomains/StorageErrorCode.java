package CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains;

import CamNecT.CamNecT_Server.global.common.response.errorcode.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StorageErrorCode implements BaseErrorCode {

    // 490xx - 입력/검증
    STORAGE_PREFIX_REQUIRED(HttpStatus.BAD_REQUEST, 49001, "저장 prefix가 비어있습니다."),
    STORAGE_EMPTY_FILE(HttpStatus.BAD_REQUEST, 49002, "빈 파일은 저장할 수 없습니다."),
    STORAGE_INVALID_PREFIX(HttpStatus.BAD_REQUEST, 49003, "허용되지 않은 prefix 입니다."),
    STORAGE_KEY_REQUIRED(HttpStatus.BAD_REQUEST, 49004, "storageKey가 비어있습니다."),

    // 491xx - 인증/토큰


    // 493xx - 권한/상태


    // 494xx - 리소스
    STORAGE_NOT_FOUND(HttpStatus.NOT_FOUND, 49401, "파일을 찾을 수 없습니다."),

    // 499xx - 충돌
    STORAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 49901, "파일 업로드에 실패했습니다."),
    STORAGE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 49902, "파일 다운로드에 실패했습니다."),
    STORAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 49903, "파일 삭제에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}