package CamNecT.CamNecT_Server.domain.users.repository;

import CamNecT.CamNecT_Server.domain.users.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    //팔로잉 수 조회
    int countByFollowerId(Long userId);

    //팔로워 수 조회
    int countByFollowingId(Long userId);

    //팔로잉 여부 조회
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
