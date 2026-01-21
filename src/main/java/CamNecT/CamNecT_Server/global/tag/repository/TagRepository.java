package CamNecT.CamNecT_Server.global.tag.repository;

import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.model.TagAttributeName;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // attribute 탭 기준 전체(활성)
    List<Tag> findByAttribute_NameAndActiveTrueOrderByCategoryAscNameAsc(TagAttributeName attributeName);

    // attribute + category 섹션 기준(활성)
    List<Tag> findByAttribute_NameAndCategoryAndActiveTrueOrderByNameAsc(TagAttributeName attributeName, String category);

    // 검색(활성) - 필요하면 attributeName 조건도 붙이기
    List<Tag> findByActiveTrueAndNameContainingOrderByNameAsc(String keyword, Pageable pageable);

    // 중복 방지용(시드/생성)
    Optional<Tag> findByAttribute_NameAndName(TagAttributeName attributeName, String name);
    boolean existsByAttribute_NameAndName(TagAttributeName attributeName, String name);

    //tagId들을 통해 실제 Tag 엔티티들을 가져옴
    List<Tag> findAllByIdIn(List<Long> tagIds);
}

