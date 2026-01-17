package CamNecT.CamNecT_Server.domain.home.service;

import CamNecT.CamNecT_Server.domain.home.dto.HomeResponse;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.users.repository.UserRepository;
import CamNecT.CamNecT_Server.global.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public HomeResponse getHome(Long userId) {
        // TODO: deviceId로 user 조회해서 displayName 결정
        // TODO: notification/coffeeChat/schedule/point/alumni/contest 각 도메인 QueryService 연결
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));
        long unreadCount = notificationService.countUnread(user.getUserId());
        return HomeResponse.of(user.getName(), unreadCount);
    }
}
