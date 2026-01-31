package CamNecT.CamNecT_Server.domain.activity.dto.response;

import CamNecT.CamNecT_Server.domain.activity.model.recruitment.TeamRecruitment;

public record RecruitmentDetailResponse(
        long userId,
        String major_kor,
        int grade,
        TeamRecruitment recruitment,
        boolean isMine,
        boolean isBookmarked
) {
}
