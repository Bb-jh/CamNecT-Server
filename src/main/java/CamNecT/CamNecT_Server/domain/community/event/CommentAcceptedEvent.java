package CamNecT.CamNecT_Server.domain.community.event;

import CamNecT.CamNecT_Server.global.notification.event.NotifiableEvent;
import CamNecT.CamNecT_Server.global.notification.model.NotificationType;

public record CommentAcceptedEvent(
        Long receiverUserId,
        Long postId,
        Long commentId,
        Long actorUserId
) implements NotifiableEvent {

    @Override
    public NotificationType type() {
        return NotificationType.COMMENT_ACCEPTED;
    }

    @Override
    public String message() {
        return "작성하신 댓글이 채택되었습니다.";
    }
}
