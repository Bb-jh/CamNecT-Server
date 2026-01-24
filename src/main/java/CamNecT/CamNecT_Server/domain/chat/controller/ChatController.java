package CamNecT.CamNecT_Server.domain.chat.controller;

import CamNecT.CamNecT_Server.domain.chat.service.ChatService;
import CamNecT.CamNecT_Server.domain.chat.dto.ChatMessage;
import CamNecT.CamNecT_Server.domain.chat.model.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessage chat(@DestinationVariable Long roomId, ChatMessage message) {

        message.setRoomId(roomId);
        //채팅 저장
        Chat chat = chatService.createChat(message);
        return ChatMessage.toDto(chat);
/*        Chat chat = chatService.createChat(roomId, message.getSender(), message.getSenderEmail(), message.getMessage());
        return ChatMessage.builder()
                .roomId(roomId)
                .sender(chat.getSender())
                .senderEmail(chat.getSenderEmail())
                .message(chat.getMessage())
                .build();*/
    }
}