package CamNecT.CamNecT_Server.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 500xx
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50000, "서버 내부 오류가 발생했습니다."),
    POST_STATS_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, 50010, "게시글 통계 데이터가 누락되었습니다."),

    // 400xx (요청이 잘못됨)
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 40000, "잘못된 요청입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 40010, "비밀번호 형식이 올바르지 않습니다."),
    TERMS_REQUIRED(HttpStatus.BAD_REQUEST, 40020, "필수 약관에 동의해야 합니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, 40001, "포인트 잔액이 부족합니다."),
    PARENT_COMMENT_NOT_IN_POST(HttpStatus.BAD_REQUEST, 40030, "부모 댓글이 해당 게시글에 속하지 않습니다."),
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, 40040, "잘못된 커서 요청입니다."),
    INVALID_REQUIRED_POINTS(HttpStatus.BAD_REQUEST, 40050, "요구 포인트 값이 올바르지 않습니다."),
    INVALID_TAG_IDS(HttpStatus.BAD_REQUEST, 40060, "유효하지 않은 태그가 포함되어 있습니다."),

    // 401xx
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "인증에 실패했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, 40101, "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, 40102, "Authorization 헤더 형식이 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 40103, "유효하지 않은 토큰입니다."),
    ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, 40104, "Access Token이 필요합니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, 40105, "로그인이 필요합니다."),

    // 403xx (권한 없음)
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "권한이 없습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, 40301, "이메일 인증이 필요합니다."),
    USER_SUSPENDED(HttpStatus.FORBIDDEN, 40302, "정지된 사용자입니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, 40310, "해당 댓글에 대한 권한이 없습니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, 40320, "해당 게시글에 대한 권한이 없습니다."),


    // 404xx (리소스 없음)
    NOT_FOUND(HttpStatus.NOT_FOUND, 40400, "리소스를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 40410, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 40411, "댓글을 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 40412, "부모 댓글을 찾을 수 없습니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, 40420, "게시판을 찾을 수 없습니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, 40421, "태그를 찾을 수 없습니다."),

    // 409xx (상태 충돌/규칙 위반)
    CONFLICT(HttpStatus.CONFLICT, 40900, "충돌이 발생하였습니다. 다시 시도해주세요."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, 40901, "이미 가입된 이메일입니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, 40902, "이미 사용 중인 아이디입니다."),
    CANNOT_REPLY_TO_DELETED_OR_HIDDEN(HttpStatus.CONFLICT, 40910, "삭제되었거나 숨김 처리된 댓글에는 답글을 달 수 없습니다."),
    COMMENT_MAX_DEPTH_EXCEEDED(HttpStatus.CONFLICT, 40911, "대댓글은 2단계까지만 허용됩니다."),
    CANNOT_DELETE_ACCEPTED_QUESTION(HttpStatus.CONFLICT, 40920, "채택된 질문은 삭제할 수 없습니다."),
    ONLY_QUESTION_CAN_ACCEPT(HttpStatus.CONFLICT, 40921, "질문 게시판에서만 채택할 수 있습니다."),
    COMMENT_NOT_IN_POST(HttpStatus.CONFLICT, 40922, "댓글이 해당 게시글에 속하지 않습니다."),
    CANNOT_ACCEPT_UNPUBLISHED_COMMENT(HttpStatus.CONFLICT, 40923, "삭제/숨김 댓글은 채택할 수 없습니다."),
    ALREADY_ACCEPTED(HttpStatus.CONFLICT, 40924, "이미 채택된 질문입니다."),
    POST_NOT_PUBLISHED(HttpStatus.CONFLICT, 40925, "게시글이 공개 상태가 아닙니다."),
    INACTIVE_TAG(HttpStatus.CONFLICT, 40926, "비활성화된 태그입니다."),

    // 405xx
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 40500, "허용되지 않은 Http 메서드입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
