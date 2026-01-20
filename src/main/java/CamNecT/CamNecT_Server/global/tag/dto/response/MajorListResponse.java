package CamNecT.CamNecT_Server.global.tag.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MajorListResponse {
    private List<MajorResponse> items;
}
