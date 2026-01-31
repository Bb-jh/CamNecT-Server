package CamNecT.CamNecT_Server.domain.profile.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateOnboardingRequest(
        String profileImageKey,
        @Size(max = 100, message = "자기소개는 100자 이하여야 합니다.")
        String bio,
        List<Long> tagIds
) {}
