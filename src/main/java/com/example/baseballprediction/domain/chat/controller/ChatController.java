package com.example.baseballprediction.domain.chat.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.baseballprediction.domain.chat.dto.ChatGiftRequestDTO;
import com.example.baseballprediction.domain.chat.dto.ChatMessage;
import com.example.baseballprediction.domain.chat.dto.ChatProfileDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.Options;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteMessage;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteResult;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.VoteResultDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteRequestDTO.ResultRatioDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteRequestDTO.VoteAction;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteRequestDTO.VoteCreation;
import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;
import com.example.baseballprediction.domain.chat.minigame.service.MiniGameService;
import com.example.baseballprediction.domain.chat.service.ChatService;
import com.example.baseballprediction.domain.member.service.MemberService;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ChatType;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ChatController {
	
	private final SimpMessageSendingOperations messagingTemplate;
	private final ChatService chatService;
	private final TeamRepository teamRepository;
	private final MemberService memberService;
	private final MiniGameService miniGameService;
	
	@MessageMapping("/chat/message")
	@SendTo("/sub/chat")
	public void messageSave(@Payload ChatMessage message,  SimpMessageHeaderAccessor headerAccessor) {
		// WebSocket 세션에서 인증 정보 가져오기
	    MemberDetails memberDetails = (MemberDetails) headerAccessor.getSessionAttributes().get("memberDetails");
	    Team team = teamRepository.findById(memberDetails.getTeamId()).orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
		ChatProfileDTO chatProfileDTO = new ChatProfileDTO(memberDetails.getName(), memberDetails.getProfileImageUrl(),team.getName());
		if(ChatType.ENTER.equals(message.getType())) {
			message.setMessage(memberDetails.getName() + "님이 입장하셨습니다.");
			message.SendProfile(chatProfileDTO);
			messagingTemplate.convertAndSend("/sub/chat/" + message.getGameId(), message);
		}else if(ChatType.NORMAL.equals(message.getType()) || ChatType.BAWWLING.equals(message.getType())) {
			message.setMessage(message.getMessage());
			message.SendProfile(chatProfileDTO);
			messagingTemplate.convertAndSend("/sub/chat/" + message.getGameId(), message);
		}
	}
	
	// 사용자가 채팅방을 나갈 때
    @DeleteMapping("/chat/{gameId}/leave")
    public ResponseEntity<ApiResponse> chatRoomRemove(@PathVariable String gameId, HttpSession session) {
        String sessionId = session.getId();
        chatService.removeMembeSessionChatRoom(sessionId, gameId);

        return ResponseEntity.ok(ApiResponse.successWithNoData());
    }
    
    // 선물하기 기능
    @PutMapping("/gift/token")
	 public ResponseEntity<ApiResponse> giftTokenAdd(@RequestBody ChatGiftRequestDTO chatGiftRequestDTO) {
		memberService.saveGiftToken(chatGiftRequestDTO.getSenderName(), chatGiftRequestDTO.getRecipientName(), chatGiftRequestDTO.getToken());

	   	return ResponseEntity.ok(ApiResponse.successWithNoData());
	}
    
    @MessageMapping("/chat/createVote")
    public void createVoteSave(@Payload VoteCreation creation, SimpMessageHeaderAccessor headerAccessor) {
    	 MemberDetails memberDetails = (MemberDetails) headerAccessor.getSessionAttributes().get("memberDetails");
    	 try {
	    	Team team = teamRepository.findById(memberDetails.getTeamId()).orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
	    	 ChatProfileDTO chatProfileDTO = new ChatProfileDTO(memberDetails.getName(), memberDetails.getProfileImageUrl(),team.getName()); 
	    	Options options = new Options(creation.getQuestion(),creation.getOption1(),creation.getOption2());
	    	MiniGame miniGame = miniGameService.saveCreateVote(creation.getGameId(), options, memberDetails.getMember().getNickname());
	        messagingTemplate.convertAndSend("/sub/chat/" + creation.getGameId(), new VoteMessage(miniGame.getId(),"투표가 시작되었습니다.",chatProfileDTO,options));
    	}catch (BusinessException e) {
    		sendErrorMessage(headerAccessor, e);
    	}
    }

    
    //  투표 할 때
    @MessageMapping("/chat/vote")
    public void performVoteAdd(@Payload VoteAction action, SimpMessageHeaderAccessor headerAccessor) {
        String nickname = ((MemberDetails) headerAccessor.getSessionAttributes().get("memberDetails")).getMember().getNickname();
        boolean result = miniGameService.addVote(action.getMiniGameId(), nickname, action.getOption());
        if(result) {
            messagingTemplate.convertAndSendToUser(nickname, "/voteResult", new VoteResult("투표해주셔서 감사합니다."));
        } else {
            messagingTemplate.convertAndSendToUser(nickname, "/voteResult", new VoteResult("이미 투표하셨습니다."));
        }
    }

    // 투표 결과 확인
    @MessageMapping("/chat/voteResult")
    public void VoteResultDetails(@Payload ResultRatioDTO resultRatioDTO, SimpMessageHeaderAccessor headerAccessor) {
    	String nickname = ((MemberDetails) headerAccessor.getSessionAttributes().get("memberDetails")).getMember().getNickname();
	    try {
		    VoteResultDTO voteResult = miniGameService.findPerformVoteAndGetResults(resultRatioDTO.getMiniGameId(), nickname);
	        messagingTemplate.convertAndSendToUser(nickname,"/voteRatioResults/" + resultRatioDTO.getMiniGameId(), voteResult);
	    }catch(BusinessException e) {
	    	sendErrorMessage(headerAccessor, e);
	    }
    }
    
    
    private void sendErrorMessage(SimpMessageHeaderAccessor headerAccessor,  BusinessException exception) {
        String nickname = ((MemberDetails) headerAccessor.getSessionAttributes().get("memberDetails")).getMember().getNickname();
        String errorMessage = exception.getMessage(); 
        messagingTemplate.convertAndSendToUser(nickname, "/chat/errors", errorMessage);
    }
    
}
