package CamNecT.CamNecT_Server.global.notification.repository;

import CamNecT.CamNecT_Server.global.notification.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findByReceiverUserIdOrderByIdDesc(Long receiverUserId, Pageable pageable);

    long countByReceiverUserIdAndReadFalse(Long receiverUserId);
}
