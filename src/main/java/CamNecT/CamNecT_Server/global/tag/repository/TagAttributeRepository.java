package CamNecT.CamNecT_Server.global.tag.repository;

import CamNecT.CamNecT_Server.global.tag.model.TagAttribute;
import CamNecT.CamNecT_Server.global.tag.model.TagAttributeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagAttributeRepository extends JpaRepository<TagAttribute, Long> {
    Optional<TagAttribute> findByName(TagAttributeName name);
    boolean existsByName(TagAttributeName name);
}
