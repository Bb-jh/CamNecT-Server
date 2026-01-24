package CamNecT.CamNecT_Server.domain.chat.dto;

import CamNecT.CamNecT_Server.domain.chat.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private Long roomId;
    private String sender;
    private String senderEmail;
    private String message;
    private LocalDateTime sendDate;

    public static ChatMessage toDto(Chat chat) {
        return ChatMessage.builder()
                .roomId(chat.getRoom().getId()) // ChatRoom 객체에서 ID만 추출
                .sender(chat.getSender())
                .senderEmail(chat.getSenderEmail())
                .message(chat.getMessage())
                .sendDate(chat.getSendDate())
                .build();
    }
}
