package CamNecT.CamNecT_Server.domain.auth.service;

import CamNecT.CamNecT_Server.domain.users.model.Users;

public interface JwtFacade {
    String createAccessToken(Users user);
    String createRefreshToken(Users user);
}
