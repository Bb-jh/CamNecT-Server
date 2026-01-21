package CamNecT.CamNecT_Server.domain.users.repository;

import CamNecT.CamNecT_Server.domain.users.model.UserInterestTagMap;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInterestTagMapRepository extends JpaRepository<UserInterestTagMap, Long> {

    @Modifying
    @Query("DELETE FROM UserInterestTagMap m WHERE m.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);

    @Query("SELECT t FROM Tag t " +
            "JOIN FETCH t.attribute ta " +
            "JOIN UserInterestTagMap m ON t.id = m.tagId " +
            "WHERE m.userId = :userId")
    List<Tag> findAllInterestTagsByUserId(@Param("userId") Long userId);
}
