package com.example.baseballprediction.domain.chat.controller;


import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.baseballprediction.domain.chat.dto.ChatProfileDTO;
import com.example.baseballprediction.domain.chat.dto.ChatRoom;
import com.example.baseballprediction.domain.chat.service.ChatService;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Controller
@RequestMapping("chat")
public class ChatRoomController {
	private final ChatService chatService;
	
	//모든 채팅방 목록 반환
	@GetMapping("/rooms")
	public  ResponseEntity<ApiResponse<List<ChatRoom>>> roomList(){
		List<ChatRoom> chatGameList =  chatService.findAllRoom();
		ApiResponse<List<ChatRoom>> response = ApiResponse.success(chatGameList);
		 return ResponseEntity.ok(response);
	}
	
	
	// 채팅방 생성
	@PostMapping("/room")
	public ResponseEntity<ApiResponse> createRoomAdd(@RequestParam Long gameId) {
		 chatService.addChatRoom(gameId);
		 return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}
	
	
	// 채팅방 입장 화면
	@GetMapping("/room/enter/{gameId}")
	public ResponseEntity<ApiResponse> roomDetail(Model model, 
			@PathVariable String gameId,
			@AuthenticationPrincipal MemberDetails memberDetails) {
		Optional<ChatProfileDTO> chatProfileDTO =  chatService.findByUsername(memberDetails.getMember().getId());
		ApiResponse<Optional<ChatProfileDTO>> response = ApiResponse.success(chatProfileDTO);
		return ResponseEntity.ok(response);
	}
	
}