package CamNecT.CamNecT_Server.global.notification.service;

import CamNecT.CamNecT_Server.global.notification.dto.PushTestResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PushTestService {

    private final PushDeviceService pushDeviceService;
    private final FCMSender fcmSender;

    @Transactional
    public PushTestResponse sendTest(Long userId) throws FirebaseMessagingException {
        var tokens = pushDeviceService.findEnabledTokens(userId);

        var result = fcmSender.sendToTokens(
                tokens,
                "Camnect 테스트 푸시",
                "FCM 연결 확인용 테스트 메시지입니다.",
                Map.of("type", "TEST")
        );

        // 무효 토큰 정리
        pushDeviceService.disableTokens(result.invalidTokens());

        return new PushTestResponse(
                result.requested(),
                result.success(),
                result.failure(),
                result.invalidTokens().size(),
                result.invalidTokens()
        );
    }
}