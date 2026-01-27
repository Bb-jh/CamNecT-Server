package CamNecT.CamNecT_Server.global.jwt;

import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtFacadeImpl implements JwtFacade {

    private final JwtUtil jwtUtil;

    @Override
    public String createAccessToken(Users user) {
        if (user == null || user.getUserId() == null || user.getRole() == null) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, new IllegalArgumentException("user/userId/role is null"));
        }
        return jwtUtil.generateAccessToken(user.getUserId(), user.getRole());
    }

    @Override
    public String createRefreshToken(Users user) {
        if (user == null || user.getUserId() == null || user.getRole() == null) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, new IllegalArgumentException("user/userId/role is null"));
        }
        return jwtUtil.generateRefreshToken(user.getUserId(), user.getRole());
    }
}
