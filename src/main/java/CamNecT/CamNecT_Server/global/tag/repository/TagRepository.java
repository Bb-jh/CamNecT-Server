package CamNecT.CamNecT_Server.global.tag.repository;

import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.model.TagAttributeName;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT utm.userId, t FROM UserTagMap utm " +
            "JOIN Tag t ON utm.tagId = t.id " +
            "WHERE utm.userId IN :userIds AND t.active = true")
    List<Object[]> findTagsWithUserIdByUserIdIn(@Param("userIds") List<Long> userIds);
}

