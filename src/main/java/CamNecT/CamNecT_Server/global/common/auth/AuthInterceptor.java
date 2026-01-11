package CamNecT.CamNecT_Server.global.common.auth;

import CamNecT.CamNecT_Server.global.jwt.JwtUtil;
import CamNecT.CamNecT_Server.global.jwt.model.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //제외할 부분은 WebMvcConfig에서 제외함
        // 1. OPTIONS 요청(Preflight)은 통과시킴 (CORS 문제 방지)
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        // 토큰 추출
        String token = extractBearer(request);
        //토큰 유효성 검사
        if(!jwtUtil.validate(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        //토큰 타입 : Access인지 검사
        if(jwtUtil.getTokenType(token) != TokenType.ACCESS)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Token이 필요합니다.");
        // username & role를 attribute로 저장

        request.setAttribute("userId", jwtUtil.getUserId(token));

        return true;
    }

    //헤더로부터 Bearer 토큰 추출
    private String extractBearer(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더 전송 형식이 잘못되었습니다.");
        }
        return header.substring(7);
    }
}