package CamNecT.CamNecT_Server.domain.chat.repository;

import CamNecT.CamNecT_Server.domain.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}