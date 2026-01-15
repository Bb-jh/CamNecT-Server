package CamNecT.CamNecT_Server.domain.institution.service;

import CamNecT.CamNecT_Server.domain.institution.dto.response.InstitutionListResponse;
import CamNecT.CamNecT_Server.domain.institution.dto.response.InstitutionResponse;
import CamNecT.CamNecT_Server.domain.institution.repository.InstitutionRepository;
import CamNecT.CamNecT_Server.global.tag.model.Institutions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionListResponse getInstitutions() {
        List<InstitutionResponse> items =
                institutionRepository.findAll().stream()
                        .map(InstitutionResponse::from)
                        .toList();

        return InstitutionListResponse.builder()
                .items(items)
                .build();
    }

    public InstitutionResponse getInstitution(Long id) {
        Institutions institution = institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기관 없음"));

        return InstitutionResponse.from(institution);
    }
}
