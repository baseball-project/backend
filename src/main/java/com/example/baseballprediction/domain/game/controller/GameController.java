package com.example.baseballprediction.domain.game.controller;

import static com.example.baseballprediction.domain.reply.dto.ReplyResponse.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.game.dto.GameResponse;
import com.example.baseballprediction.domain.game.dto.GameResponse.GameDtoDaily;
import com.example.baseballprediction.domain.game.service.GameService;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRequest.GameVoteRequestDTO;
import com.example.baseballprediction.domain.gamevote.service.GameVoteService;
import com.example.baseballprediction.domain.reply.dto.ReplyRequest;
import com.example.baseballprediction.domain.reply.dto.ReplyResponse;
import com.example.baseballprediction.domain.reply.service.ReplyService;
import com.example.baseballprediction.domain.replylike.service.ReplyLikeService;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {
	private final GameService gameService;
	private final ReplyService replyService;
	private final ReplyLikeService replyLikeService;
	private final GameVoteService gameVoteService;

	//오늘의 승부예측 경기 list 조회
	@GetMapping("")
	public ResponseEntity<ApiResponse<List<GameDtoDaily>>> gameDailyTodayList() {

		List<GameDtoDaily> gameDtoDailyList = gameService.findDailyGame();

		ApiResponse<List<GameDtoDaily>> response = ApiResponse.success(gameDtoDailyList);

		return ResponseEntity.ok(response);

	}

	//승부 예측 댓글 조회 
	@GetMapping("/daily-replies")
	public ResponseEntity<ApiResponse<Page<ReplyDTO>>> replyGameList(
		@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "15") int item) {

		Page<ReplyDTO> replyGameList = replyService.findRepliesByType(ReplyType.GAME, page, item,
			memberDetails.getUsername());

		ApiResponse<Page<ReplyDTO>> response = ApiResponse.success(replyGameList);

		return ResponseEntity.ok(response);
	}

	//승부예측 댓글 작성
	@PostMapping("/daily-reply")
	public ResponseEntity<ApiResponse> gameReplyAdd(@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody
	ReplyRequest.ReplyDTO replyDTO) {
		replyService.addReply(ReplyType.GAME, memberDetails.getUsername(), replyDTO.getContent());

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}

	//승부예측 댓글 좋아요
	@PostMapping("/daily-reply/{replyId}/like")
	public ResponseEntity<ApiResponse> gameReplyLikeAdd(@AuthenticationPrincipal MemberDetails memberDetails,
		@PathVariable Long replyId) {

		replyLikeService.saveReplyLike(memberDetails.getUsername(), replyId);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@DeleteMapping("/daily-reply/{replyId}/like")
	public ResponseEntity<ApiResponse> gameReplyLikeCancel(@AuthenticationPrincipal MemberDetails memberDetails,
		@PathVariable Long replyId) {
		replyLikeService.deleteReplyLike(memberDetails.getUsername(), replyId);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	//승부예측 투표
	@PostMapping("/{gameId}/vote")
	public ResponseEntity<ApiResponse> gameVoteAdd(@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestBody GameVoteRequestDTO gameVoteRequestDTO,
		@PathVariable Long gameId) {

		gameVoteService.addGameVote(memberDetails.getUsername(), gameId, gameVoteRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}

	//승부예측 투표 변경 
	@PutMapping("/voteUpdate/{gameId}")
	public ResponseEntity<ApiResponse> gameVoteModify(@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestBody GameVoteRequestDTO gameVoteRequestDTO,
		@PathVariable Long gameId) {
		gameVoteService.modifyGameVote(memberDetails.getUsername(), gameId, gameVoteRequestDTO);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	//승부예측 투표 취소 
	@DeleteMapping("/voteDelete/{gameId}")
	public ResponseEntity<ApiResponse> gameVoteRemove(@AuthenticationPrincipal MemberDetails memberDetails,
		@PathVariable Long gameId) {
		gameVoteService.removeGameVote(gameId, memberDetails.getUsername());

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@GetMapping("/daily-replies/{replyId}/sub")
	public ResponseEntity<ApiResponse<List<ReplyResponse.ReplyDTO>>> replySubList(@PathVariable Long replyId) {
		List<ReplyResponse.ReplyDTO> replies = replyService.findSubReplies(replyId);

		ApiResponse<List<ReplyResponse.ReplyDTO>> response = ApiResponse.success(replies);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/daily-replies/{replyId}/sub")
	public ResponseEntity<ApiResponse> replySubAdd(@PathVariable Long replyId,
		@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody ReplyRequest.ReplyDTO replyDTO) {
		replyService.addSubReply(replyId, ReplyType.GAME, memberDetails.getUsername(), replyDTO.getContent());

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}

	@GetMapping("/past")
	public ResponseEntity<ApiResponse<List<GameResponse.PastGameDTO>>> gameWeekList(@RequestParam String startDate,
		@RequestParam String endDate,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		List<GameResponse.PastGameDTO> gameResults = gameService.findGameResult(memberDetails.getUsername(), startDate,
			endDate);

		ApiResponse<List<GameResponse.PastGameDTO>> response = ApiResponse.success(gameResults);

		return ResponseEntity.ok(response);
	}

}
