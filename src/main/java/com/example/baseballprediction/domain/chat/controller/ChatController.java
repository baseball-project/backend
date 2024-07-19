package com.example.baseballprediction.domain.chat.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.chat.dto.ChatEventDTO.ChatLeaveMessage;
import com.example.baseballprediction.domain.chat.dto.ChatEventDTO.ChatMessage;
import com.example.baseballprediction.domain.chat.dto.ChatEventDTO.ChatProfileDTO;
import com.example.baseballprediction.domain.chat.dto.ChatRequestDTO.ChatGiftRequestDTO;
import com.example.baseballprediction.domain.chat.dto.ChatRequestDTO.ChatLeaveRequest;
import com.example.baseballprediction.domain.chat.dto.GameTeamType;
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
import com.example.baseballprediction.domain.member.service.ProfileService;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ChatMessageType;
import com.example.baseballprediction.global.constant.ChatType;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.Status;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ChatController {

	private final SimpMessageSendingOperations messagingTemplate;
	private final ChatService chatService;
	private final TeamRepository teamRepository;
	private final ProfileService profileService;
	private final MiniGameService miniGameService;

	@MessageMapping("/chat/message")
	@SendTo("/sub/chat")
	public void messageSave(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
		// WebSocket 세션에서 인증 정보 가져오기
		MemberDetails memberDetails = (MemberDetails)headerAccessor.getSessionAttributes().get("memberDetails");
		Team team = teamRepository.findById(memberDetails.getTeamId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
		ChatProfileDTO chatProfileDTO = new ChatProfileDTO(memberDetails.getName(), memberDetails.getProfileImageUrl(),
			team.getName());
		GameTeamType gameTeamType = chatService.findDailyGameTeamType(message.getGameId(),
			memberDetails.getMember().getId());
		if (ChatType.ENTER.equals(message.getType())) {
			message.setMessage(ChatMessageType.getEnterMessage(ChatMessageType.ENTER_MESSAGE, memberDetails.getName()));
			message.sendProfile(chatProfileDTO);
			message.setTeamType(gameTeamType.getTeamType());
			messagingTemplate.convertAndSend("/sub/chat/" + message.getGameId(), message);
		} else if (ChatType.NORMAL.equals(message.getType()) || ChatType.BAWWLING.equals(message.getType())) {
			message.setMessage(message.getMessage());
			message.sendProfile(chatProfileDTO);
			message.setTeamType(gameTeamType.getTeamType());
			messagingTemplate.convertAndSend("/sub/chat/" + message.getGameId(), message);
		}
	}

	// 선물하기 기능
	@PutMapping("/gift/token")
	public ResponseEntity<ApiResponse> giftTokenAdd(@Valid @RequestBody ChatGiftRequestDTO chatGiftRequestDTO,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		profileService.saveGiftToken(memberDetails.getMember().getId(), chatGiftRequestDTO);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@MessageMapping("/chat/createVote")
	public void createVoteSave(@Payload VoteCreation creation, SimpMessageHeaderAccessor headerAccessor) {
		MemberDetails memberDetails = (MemberDetails)headerAccessor.getSessionAttributes().get("memberDetails");
		Team team = teamRepository.findById(memberDetails.getTeamId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
		ChatProfileDTO chatProfileDTO = new ChatProfileDTO(memberDetails.getName(),
			memberDetails.getProfileImageUrl(), team.getName());
		Options options = new Options(creation.getQuestion(), creation.getOption1(), creation.getOption2());
		MiniGame miniGame = miniGameService.saveCreateVote(creation.getGameId(), options,
			memberDetails.getMember().getNickname());
		LocalDateTime startedAt = miniGame.getStartedAt();

		if (miniGame.getStatus() == Status.PROGRESS) {
			messagingTemplate.convertAndSend("/sub/chat/" + creation.getGameId(),
				new VoteMessage(miniGame.getId(), ChatMessageType.VOTE_STARTED, chatProfileDTO, options, startedAt));
			return;
		}

		if (miniGame.getStatus() == Status.READY) {
			messagingTemplate.convertAndSendToUser(memberDetails.getMember().getNickname(),
				"/chat/" + creation.getGameId(),
				new VoteMessage(miniGame.getId(), ChatMessageType.VOTE_CREATED, chatProfileDTO, options, startedAt));
			return;
		}
	}

	//  투표 할 때
	@MessageMapping("/chat/vote")
	public void performVoteAdd(@Payload VoteAction action, SimpMessageHeaderAccessor headerAccessor) {
		String nickname = ((MemberDetails)headerAccessor.getSessionAttributes().get("memberDetails")).getMember()
			.getNickname();
		boolean result = miniGameService.addVote(action.getMiniGameId(), nickname, action.getOption());
		if (result) {
			messagingTemplate.convertAndSendToUser(nickname, "/voteResult",
				new VoteResult(ChatMessageType.THANK_YOU_FOR_VOTING.getMessage()));
		} else {
			messagingTemplate.convertAndSendToUser(nickname, "/voteResult",
				new VoteResult(ChatMessageType.ALREADY_VOTED.getMessage()));
		}
	}

	// 투표 결과 확인
	@MessageMapping("/chat/voteResult")
	public void VoteResultDetails(@Payload ResultRatioDTO resultRatioDTO, SimpMessageHeaderAccessor headerAccessor) {
		String nickname = ((MemberDetails)headerAccessor.getSessionAttributes().get("memberDetails")).getMember()
			.getNickname();
		VoteResultDTO voteResult = miniGameService.findPerformVoteAndGetResults(resultRatioDTO.getMiniGameId(),
			nickname);
		messagingTemplate.convertAndSend("/sub/voteRatioResults/" + resultRatioDTO.getMiniGameId(),
			voteResult);
	}

	@MessageMapping("/chat/leave")
	public void leaveChatRoomRemove(@Payload ChatLeaveRequest leaveRequest, SimpMessageHeaderAccessor headerAccessor) {
		String sessionId = headerAccessor.getSessionId();
		String nickname = ((MemberDetails)headerAccessor.getSessionAttributes().get("memberDetails")).getMember()
			.getNickname();
		Long gameId = leaveRequest.getGameId();

		chatService.removeMembeSessionChatRoom(sessionId, gameId);

		ChatLeaveMessage leaveMessage = new ChatLeaveMessage(nickname, ChatMessageType.LEAVE_MESSAGE);
		messagingTemplate.convertAndSend("/sub/chat/" + gameId, leaveMessage);
	}

	@MessageExceptionHandler(BusinessException.class)
	public void handleBusinessException(BusinessException exception, SimpMessageHeaderAccessor headerAccessor) {
		String errorMessage = exception.getMessage();
		String nickname = ((MemberDetails)headerAccessor.getSessionAttributes().get("memberDetails")).getMember()
			.getNickname();
		messagingTemplate.convertAndSendToUser(nickname, "/chat/errors", errorMessage);
	}

}
