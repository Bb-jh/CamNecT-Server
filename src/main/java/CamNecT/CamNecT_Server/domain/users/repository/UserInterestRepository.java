package CamNecT.CamNecT_Server.domain.users.repository;

import CamNecT.CamNecT_Server.domain.users.model.UserInterest;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    // 특정 유저의 관심분야 ID 리스트 조회
    @Query("SELECT ui.interests.id FROM UserInterest ui WHERE ui.user.id = :userId")
    List<Long> findInterestIdsByUserId(@Param("userId") Long userId);

    //추천순 & 태그 필터링된 유저 조회
    @Query("""
                SELECT ui.user.userId
                FROM UserInterest ui
                WHERE ui.user.userId != :myUserId
                  AND ui.interests.id IN :myInterestIds
                  AND (
                        :tagList IS NULL
                        OR ui.user.userId IN (
                            SELECT utm.userId
                            FROM UserTagMap utm
                            WHERE utm.tagId IN :tagList
                        )
                  )
                GROUP BY ui.user.userId
                ORDER BY COUNT(ui.id) DESC
            """)
    List<Long> findRecommendedUserIds(
            @Param("myUserId") Long myUserId,
            @Param("myInterestIds") List<Long> myInterestIds,
            @Param("tagList") List<Long> tagList
    );

    Long user(Users user);
}