package CamNecT.CamNecT_Server.domain.auth.dto.login;

public record LoginResponse(
        String tokenType,     // "Bearer"
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInMs,
        long refreshTokenExpiresInMs,
        Long userId,
        String status         // ADMIN_PENDING / ACTIVE ...
) {
}
