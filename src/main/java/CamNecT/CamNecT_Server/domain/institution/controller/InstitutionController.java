package CamNecT.CamNecT_Server.domain.institution.controller;

import CamNecT.CamNecT_Server.domain.institution.dto.response.InstitutionListResponse;
import CamNecT.CamNecT_Server.domain.institution.dto.response.InstitutionResponse;
import CamNecT.CamNecT_Server.domain.institution.service.InstitutionService;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    // 대학 전체 조회
    @GetMapping
    public ApiResponse<InstitutionListResponse> getInstitutions() {
        return ApiResponse.success(institutionService.getInstitutions());
    }

    // 대학 단건 조회
    @GetMapping("/{institutionId}")
    public ApiResponse<InstitutionResponse> getInstitution(
            @PathVariable Long institutionId
    ) {
        return ApiResponse.success(institutionService.getInstitution(institutionId));
    }
}
