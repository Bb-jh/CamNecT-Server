package CamNecT.CamNecT_Server.domain.chat.controller;

import CamNecT.CamNecT_Server.domain.chat.service.ChatService;
import CamNecT.CamNecT_Server.domain.chat.dto.ChatMessage;
import CamNecT.CamNecT_Server.domain.chat.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;

    /**
     * 채팅방 리스트 화면
     */
    @GetMapping("/roomList")
    public String roomList(Model model) {
        List<ChatRoom> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "roomList"; // resources/templates/roomList.html
    }

    /**
     * 방 만들기 화면
     */
    @GetMapping("/roomForm")
    public String roomForm() {
        return "roomForm"; // resources/templates/roomForm.html
    }

    /**
     * 방 생성 처리 (POST)
     */
    @PostMapping("/room")
    public String createRoom(@RequestParam String name, RedirectAttributes rattr) {
        ChatRoom room = chatService.createRoom(name);
        rattr.addFlashAttribute("message", room.getName() + " 방이 개설되었습니다.");
        return "redirect:/roomList";
    }

    /**
     * 채팅방 입장 화면
     */
    @GetMapping("/chat/room/{roomId}")
    public String joinRoom(@PathVariable Long roomId, Model model) {
        // Service에서 DTO로 변환된 리스트를 받습니다.
        List<ChatMessage> chatList = chatService.findAllChatByRoomId(roomId);
        ChatRoom room = chatService.findRoomById(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", room.getName());
        model.addAttribute("chatList", chatList);

        return "chatRoom"; // resources/templates/chatRoom.html
    }
}
