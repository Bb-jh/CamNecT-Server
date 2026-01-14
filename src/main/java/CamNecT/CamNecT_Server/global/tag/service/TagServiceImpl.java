package CamNecT.CamNecT_Server.global.tag.service;

import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.model.TagAttribute;
import CamNecT.CamNecT_Server.global.tag.model.TagAttributeName;
import CamNecT.CamNecT_Server.global.tag.repository.TagAttributeRepository;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagAttributeRepository tagAttributeRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Tag> list(TagAttributeName attribute) {
        return tagRepository.findByAttribute_NameAndActiveTrueOrderByCategoryAscNameAsc(attribute);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tag> list(TagAttributeName attribute, String category) {
        return tagRepository.findByAttribute_NameAndCategoryAndActiveTrueOrderByNameAsc(attribute, category);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tag> search(String keyword, int limit) {
        int size = Math.min(Math.max(limit, 1), 50);
        return tagRepository.findByActiveTrueAndNameContainingOrderByNameAsc(keyword, PageRequest.of(0, size));
    }

    @Transactional
    @Override
    public void seedDefaults() {
        // 1) TagAttribute 3종 보장
        TagAttribute dept = ensureAttribute(TagAttributeName.DEPARTMENT);
        TagAttribute topic = ensureAttribute(TagAttributeName.TOPIC);
        TagAttribute custom = ensureAttribute(TagAttributeName.CUSTOM);

        // 2) TOPIC 기본 태그(마이페이지 예시 기준) - 원치 않으면 여기만 비우시면 됩니다.
        Map<String, List<String>> topicSeeds = new LinkedHashMap<>();
        topicSeeds.put("학업", List.of("공부", "시험"));
        topicSeeds.put("대외활동", List.of("동아리", "멘토링", "친목", "팀원 모집"));
        topicSeeds.put("진로", List.of("취업", "창업", "스펙", "포트폴리오", "자기소개서"));
        topicSeeds.put("기타", List.of("정보", "학내 이슈", "거리"));

        for (var entry : topicSeeds.entrySet()) {
            String category = entry.getKey();
            for (String name : entry.getValue()) {
                ensureTag(topic, TagAttributeName.TOPIC, name, category);
            }
        }

        // DEPARTMENT(학과/전공)는 majors 테이블과 연동할 거면 나중에 별도 sync 로직에서 넣는 걸 권장합니다.
        // CUSTOM은 유저 생성 태그를 허용할 때 쓰면 됩니다.
    }

    private TagAttribute ensureAttribute(TagAttributeName name) {
        return tagAttributeRepository.findByName(name)
                .orElseGet(() -> {
                    try {
                        return tagAttributeRepository.save(
                                TagAttribute.builder().name(name).build()
                        );
                    } catch (DataIntegrityViolationException e) {
                        // 동시 부팅 경쟁 대비
                        return tagAttributeRepository.findByName(name).orElseThrow();
                    }
                });
    }

    private void ensureTag(TagAttribute attribute, TagAttributeName attributeName, String name, String category) {
        if (tagRepository.existsByAttribute_NameAndName(attributeName, name)) return;

        try {
            tagRepository.save(Tag.create(attribute, name, category));
        } catch (DataIntegrityViolationException e) {
            // 중복 insert 경쟁 방어
        }
    }
}