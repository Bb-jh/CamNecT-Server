package CamNecT.CamNecT_Server.domain.alumni.service;

import CamNecT.CamNecT_Server.domain.alumni.dto.AlumniPreviewResponse;
import CamNecT.CamNecT_Server.domain.users.model.UserProfile;
import CamNecT.CamNecT_Server.domain.users.model.UserTagMap;
import CamNecT.CamNecT_Server.domain.users.repository.UserInterestRepository;
import CamNecT.CamNecT_Server.domain.users.repository.UserProfileRepository;
import CamNecT.CamNecT_Server.domain.users.repository.UserTagMapRepository;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumniService {

    private final UserInterestRepository userInterestRepository;
    private final UserTagMapRepository userTagMapRepository;
    private final TagRepository tagRepository;
    private final UserProfileRepository userProfileRepository;

    public List<AlumniPreviewResponse> searchAlumni(Long myUserId, List<Long> tagList) {

        //유저 필터링
        List<Long> myInterestIds = userInterestRepository.findInterestIdsByUserId(myUserId);

        myInterestIds = (tagList == null || tagList.isEmpty()) ? null : tagList;

        List<Long> AlumniIdList = userInterestRepository.findRecommendedUserIds(myUserId, myInterestIds, tagList);

        //dto 감싸기


        //유저 필터링
        List<Long> recommendedIds = userInterestRepository.findRecommendedUserIds(myUserId, myInterestIds, tagList);
        if (recommendedIds.isEmpty()) return Collections.emptyList();

        //일괄 조회 (N+1 방지)

        List<UserProfile> profiles = userProfileRepository.findAllByUserIdIn(recommendedIds);
        List<UserTagMap> tagMaps = userTagMapRepository.findAllByUserIdIn(recommendedIds);

        //전체 태그 정보 가져오기
        List<Long> allTagIds = tagMaps.stream().map(UserTagMap::getTagId).distinct().toList();
        List<Tag> allTags = tagRepository.findAllByIdIn(allTagIds);

        //빠른 조회를 위한 Map 변환
        Map<Long, UserProfile> profileMap = profiles.stream()
                .collect(Collectors.toMap(UserProfile::getUserId, p -> p));

        Map<Long, Tag> tagDataMap = allTags.stream()
                .collect(Collectors.toMap(Tag::getId, t -> t));

        // 유저별로 태그 리스트 그룹화: Map<UserId, List<Tag>>
        Map<Long, List<Tag>> userTagsGroupMap = tagMaps.stream()
                .collect(Collectors.groupingBy(
                        UserTagMap::getUserId,
                        Collectors.mapping(map -> tagDataMap.get(map.getTagId()), Collectors.toList())
                ));

        //원래 정렬된 ID 순서대로 DTO 조립
        return recommendedIds.stream()
                .map(id -> new AlumniPreviewResponse(
                        id,
                        profileMap.get(id),
                        userTagsGroupMap.getOrDefault(id, Collections.emptyList())
                ))
                .toList();

    }

}
