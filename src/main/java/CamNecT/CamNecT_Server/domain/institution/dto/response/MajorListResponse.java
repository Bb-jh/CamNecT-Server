package CamNecT.CamNecT_Server.domain.institution.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MajorListResponse {
    private List<MajorResponse> items;
}
