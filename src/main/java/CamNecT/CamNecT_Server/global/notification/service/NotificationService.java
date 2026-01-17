package CamNecT.CamNecT_Server.global.notification.service;

import CamNecT.CamNecT_Server.global.notification.model.Notification;
import CamNecT.CamNecT_Server.global.notification.model.NotificationType;
import CamNecT.CamNecT_Server.global.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void create(Long receiverUserId,
                       Long actorUserId,
                       NotificationType type,
                       String message,
                       Long postId,
                       Long commentId) {

        notificationRepository.save(
                Notification.of(receiverUserId, actorUserId, type, message, postId, commentId)
        );
    }
}