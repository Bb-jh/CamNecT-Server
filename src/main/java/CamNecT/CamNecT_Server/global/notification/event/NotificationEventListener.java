package CamNecT.CamNecT_Server.global.notification.event;

import CamNecT.CamNecT_Server.global.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotifiableEvent e) {
        notificationService.create(
                e.receiverUserId(),
                e.actorUserId(),
                e.type(),
                e.message(),
                e.postId(),
                e.commentId()
        );
    }
}