package CamNecT.CamNecT_Server.global.tag.service;

import CamNecT.CamNecT_Server.global.tag.dto.response.InstitutionListResponse;
import CamNecT.CamNecT_Server.global.tag.dto.response.InstitutionResponse;
import CamNecT.CamNecT_Server.global.tag.repository.InstitutionRepository;
import CamNecT.CamNecT_Server.global.tag.model.Institutions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionListResponse getInstitutions() {
        List<InstitutionResponse> items =
                institutionRepository.findAllByOrderByInstitutionNameKorAsc().stream()
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
