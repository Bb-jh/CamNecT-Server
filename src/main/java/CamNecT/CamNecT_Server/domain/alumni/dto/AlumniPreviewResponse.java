package CamNecT.CamNecT_Server.domain.alumni.dto;

import CamNecT.CamNecT_Server.domain.users.model.UserProfile;
import CamNecT.CamNecT_Server.global.tag.model.Tag;

import java.util.List;

public record AlumniPreviewResponse(
        Long userId,
        UserProfile userProfile,
        List<Tag> tagList
) {
}
