package CamNecT.CamNecT_Server.domain.alumni.service;

import CamNecT.CamNecT_Server.domain.alumni.dto.AlumniPreviewResponse;
import CamNecT.CamNecT_Server.domain.alumni.repository.AlumniRepository;
import CamNecT.CamNecT_Server.domain.users.model.UserProfile;
import CamNecT.CamNecT_Server.domain.users.repository.UserProfileRepository;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import CamNecT.CamNecT_Server.global.tag.service.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumniService {

    private final TagRepository tagRepository;
    private final UserProfileRepository userProfileRepository;
    private final TagServiceImpl tagService;
    private final AlumniRepository alumniRepository;

    public List<AlumniPreviewResponse> searchAlumni(Long userId, String name, List<Long> tagIdList) {

        // 1. 조건에 맞는 유저 ID 목록 조회
        int tagCount = (tagIdList == null) ? 0 : tagIdList.size();
        List<Long> targetIds = alumniRepository.findAlumniIdsByConditions(userId, name, tagIdList, tagCount);

        if (targetIds.isEmpty()) return List.of();

        // 2. 프로필 정보 조회 및 Map 변환
        Map<Long, UserProfile> profileMap = userProfileRepository.findAllById(targetIds).stream()
                .collect(Collectors.toMap(UserProfile::getUserId, p -> p));

        // 3. 태그 정보 조회 및 Map 변환 (통합 로직)
        List<Object[]> tagResults = tagRepository.findTagsWithUserIdByUserIdIn(targetIds);
        Map<Long, List<Tag>> tagMap = tagResults.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0], // userId
                        Collectors.mapping(row -> (Tag) row[1], Collectors.toList()) // Tag 객체
                ));

        // 4. targetIds의 정렬 순서를 유지하며 최종 DTO 생성
        return targetIds.stream()
                .map(id -> new AlumniPreviewResponse(
                        id,
                        profileMap.get(id),
                        tagMap.getOrDefault(id, List.of())
                ))
                .toList();
    }

}
