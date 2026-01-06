package CamNecT.CamNecT_Server.domain.users.repository;

import CamNecT.CamNecT_Server.domain.users.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    //UserProfile에 연관된 정보를 미리 로딩하는 쿼리
    @Query("SELECT up FROM UserProfile up " +
            "JOIN FETCH up.user " +
            "JOIN FETCH up.institution " +
            "JOIN FETCH up.major m " +
            "JOIN FETCH m.college " +
            "WHERE up.userId = :userId")
    Optional<UserProfile> findProfileWithAllDetails(@Param("userId") Long userId);
}