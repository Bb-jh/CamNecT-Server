package CamNecT.CamNecT_Server.global.notification.event;

import CamNecT.CamNecT_Server.global.notification.model.NotificationType;

public interface NotifiableEvent {
    Long receiverUserId();
    Long actorUserId();
    NotificationType type();

    // 링크(딥링크)용 식별자들 - 필요 없으면 null 가능
    Long postId();
    Long commentId();

    // 메시지 생성(간단히 고정문구면 여기서 반환)
    String message();
}
