package CamNecT.CamNecT_Server.domain.institution.service;

import CamNecT.CamNecT_Server.domain.institution.dto.response.MajorListResponse;
import CamNecT.CamNecT_Server.domain.institution.dto.response.MajorResponse;
import CamNecT.CamNecT_Server.domain.institution.repository.InstitutionRepository;
import CamNecT.CamNecT_Server.domain.institution.repository.MajorRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.ErrorCode;
import CamNecT.CamNecT_Server.global.tag.model.Institutions;
import CamNecT.CamNecT_Server.global.tag.model.Majors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorService {

    private final InstitutionRepository institutionRepository;
    private final MajorRepository majorRepository;

    public MajorListResponse getMajors(Long institutionId) {
        Institutions institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new RuntimeException("해당 학교가 존재하지 않습니다.")));

        List<MajorResponse> items = majorRepository
                .findByInstitution_InstitutionId(institutionId)
                .stream()
                .map(MajorResponse::from)
                .toList();

        return MajorListResponse.builder()
                .items(items)
                .build();
    }

    public MajorResponse getMajor(Long institutionId, Long majorId) {
        Majors majors = majorRepository.findByMajorIdAndInstitution_InstitutionId(majorId, institutionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new RuntimeException("해당 학교가 존재하지 않습니다.")));

        return MajorResponse.from(majors);
    }
}
