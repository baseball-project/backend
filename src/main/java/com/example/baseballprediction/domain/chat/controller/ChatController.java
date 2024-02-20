package com.example.baseballprediction.domain.chat.controller;


import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;
import com.example.baseballprediction.domain.chat.dto.ChatMessage;
import com.example.baseballprediction.global.constant.ChatType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ChatController {
	
	private final SimpMessageSendingOperations messagingTemplate;
	
	@MessageMapping("/chat/message")
	@SendTo("/sub/chat")
	public void messageSave(ChatMessage message,@Header("nickname") String nickname,
			@Header("profileImageUrl") String profileImageUrl,
			@Header("teamName") String teamName) {
		if(ChatType.ENTER.equals(message.getType())) {
			if (nickname == null) {
				nickname = "Unknown"; // header가 없을 경우 기본값 설정
		    }
			message.SendMessage(nickname + "님이 입장하셨습니다.");
			message.SendProfile(nickname, profileImageUrl, teamName);
			messagingTemplate.convertAndSend("/sub/chat/" + message.getGameId(), message);
		}else if(ChatType.NOMAL.equals(message.getType())) {
			message.SendProfile(nickname, profileImageUrl, teamName);
			message.SendMessage(message.getMessage());
			
			messagingTemplate.convertAndSend("/sub/chat/" + message.getGameId(), message);
		}else if(ChatType.BAWWLING.equals(message.getType())) {
			message.SendProfile(nickname, profileImageUrl, teamName);
			message.SendMessage(message.getMessage());
			messagingTemplate.convertAndSend("/sub/chat/" + message.getGameId(), message);
		}
	}
	
}
