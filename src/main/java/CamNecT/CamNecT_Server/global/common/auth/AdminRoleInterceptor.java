package CamNecT.CamNecT_Server.global.common.auth;

import CamNecT.CamNecT_Server.domain.users.model.UserRole;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.ErrorCode;
import CamNecT.CamNecT_Server.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AdminRoleInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, new IllegalArgumentException("Authorization 헤더가 존재하지 않습니다."));
        }

        String token = extractBearerToken(authHeader);

        UserRole role = jwtUtil.getRole(token);
        if (role != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN, new IllegalArgumentException("관리자 권한이 필요합니다."));
        }
        return true;
    }

    private String extractBearerToken(String header) {
        String prefix = "Bearer ";
        if (!header.startsWith(prefix)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, new IllegalArgumentException("Authorization 헤더 형식이 올바르지 않습니다."));
        }
        return header.substring(prefix.length()).trim();
    }
}