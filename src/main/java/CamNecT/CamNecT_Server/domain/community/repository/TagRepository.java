package CamNecT.CamNecT_Server.domain.community.repository;

import CamNecT.CamNecT_Server.domain.community.model.Tag;
import CamNecT.CamNecT_Server.domain.community.model.enums.TagType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository  extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByTypeAndName(TagType type, String name);
    boolean existsByTypeAndName(TagType type, String name);
}
