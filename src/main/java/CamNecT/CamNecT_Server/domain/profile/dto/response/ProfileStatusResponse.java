package CamNecT.CamNecT_Server.domain.profile.dto.response;

import CamNecT.CamNecT_Server.domain.users.model.UserStatus;

public record ProfileStatusResponse(
        UserStatus status
) {}
