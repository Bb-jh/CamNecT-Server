package CamNecT.CamNecT_Server.global.tag.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InstitutionListResponse {
    private List<InstitutionResponse> items;
}