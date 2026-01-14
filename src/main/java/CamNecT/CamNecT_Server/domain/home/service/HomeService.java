package CamNecT.CamNecT_Server.domain.home.service;

import CamNecT.CamNecT_Server.domain.home.dto.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {

    public HomeResponse getHome(String deviceId) {
        // TODO: deviceId로 user 조회해서 displayName 결정
        // TODO: notification/coffeeChat/schedule/point/alumni/contest 각 도메인 QueryService 연결

        String displayName = "부회장"; // 임시값(프론트 UI 확인용)
        return HomeResponse.empty(displayName);
    }
}
