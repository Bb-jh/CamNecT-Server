package CamNecT.CamNecT_Server.global.common.response.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VerificationErrorCode implements BaseErrorCode {

    // 440xx
    DOCUMENTS_REQUIRED(HttpStatus.BAD_REQUEST, 44020, "서류 파일은 최소 1개 필요합니다."),
    TOO_MANY_FILES(HttpStatus.BAD_REQUEST, 44021, "업로드 가능한 파일 개수(3)를 초과했습니다."),
    EMPTY_FILE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 44022, "빈 파일은 업로드할 수 없습니다."),

    // 441 / 441 (업로드 관련)
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, 44123, "파일 크기가 제한을 초과했습니다."),
    UNSUPPORTED_CONTENT_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, 44124, "허용되지 않는 파일 형식입니다."),

    // 444xx
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, 44401, "요청을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 44402, "유저를 찾을 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, 44403, "파일을 찾을 수 없습니다."),

    // 449xx
    PENDING_ALREADY_EXISTS(HttpStatus.CONFLICT, 44930, "이미 처리 대기(PENDING) 중인 요청이 있습니다."),
    ONLY_PENDING_CAN_REVIEW(HttpStatus.CONFLICT, 44910, "PENDING 상태만 처리할 수 있습니다."),

    // 440xx (관리자 반려)
    REJECT_REASON_REQUIRED(HttpStatus.BAD_REQUEST, 44011, "반려 사유가 필요합니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}