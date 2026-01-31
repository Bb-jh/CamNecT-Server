package CamNecT.CamNecT_Server.domain.activity.dto.response;

import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivity;
import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivityAttachment;
import CamNecT.CamNecT_Server.domain.activity.model.recruitment.TeamRecruitment;
import CamNecT.CamNecT_Server.global.tag.model.Tag;

import java.util.List;

public record ActivityDetailResponse(
        boolean isMine,
        ExternalActivity activity,
        List<ExternalActivityAttachment> attachment,
        List<Tag> tagList,
        List<TeamRecruitment> recruitmentList
        ) {
}
