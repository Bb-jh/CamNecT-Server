package CamNecT.CamNecT_Server.domain.chat.service;


import CamNecT.CamNecT_Server.domain.chat.dto.ChatMessage;
import CamNecT.CamNecT_Server.domain.chat.model.Chat;
import CamNecT.CamNecT_Server.domain.chat.model.ChatRoom;
import CamNecT.CamNecT_Server.domain.chat.repository.ChatRepository;
import CamNecT.CamNecT_Server.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRoomRepository roomRepository;
    private final ChatRepository chatRepository;

    public List<ChatRoom> findAllRoom() {
        return roomRepository.findAll();
    }

    public ChatRoom findRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }


    @Transactional
    public ChatRoom createRoom(String name) {
        return roomRepository.save(ChatRoom.createRoom(name));
    }

    @Transactional
    public Chat createChat(ChatMessage chatDto) {
        ChatRoom room = roomRepository.findById(chatDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방이 없습니다."));

        return chatRepository.save(Chat.createChat(room, chatDto.getSender(), chatDto.getSenderEmail(), chatDto.getMessage()));
    }


    // 채팅방 채팅내용 불러오기
    public List<ChatMessage> findAllChatByRoomId(Long roomId) {
        List<Chat> chatEntities = chatRepository.findAllByRoomId(roomId);

        // 스트림을 사용하여 반복문 처리 (Entity 리스트 -> DTO 리스트 변환)
        return chatEntities.stream()
                .map(ChatMessage::toDto)
                .toList();

    }

}