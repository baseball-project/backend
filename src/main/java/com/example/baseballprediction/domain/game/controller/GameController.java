package com.example.baseballprediction.domain.game.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.baseballprediction.domain.game.dto.GameReplyLikeProjection.GameListDTO;
import com.example.baseballprediction.domain.game.dto.GameResponse.GameDtoDaily;
import com.example.baseballprediction.domain.game.service.GameService;
import com.example.baseballprediction.domain.reply.dto.ReplyRequest;
import com.example.baseballprediction.domain.reply.service.ReplyService;
import com.example.baseballprediction.domain.replylike.service.ReplyLikeService;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.security.auth.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {
	
	private final GameService gameService;
	private final ReplyService replyService;
	private final ReplyLikeService replyLikeService;
	
	//오늘의 승부예측 경기 list 조회
	@GetMapping("")
	public ResponseEntity<ApiResponse<List<GameDtoDaily>>> gameDailyTodayList() {
		
		List<GameDtoDaily> gameDtoDailyList = gameService.findDailyGame();
		
		ApiResponse<List<GameDtoDaily>> response = ApiResponse.success(gameDtoDailyList);
		
		return ResponseEntity.ok(response);
		
	}
	
	//승부 예측 댓글 조회 
	@GetMapping("/daily-replies")
	public ResponseEntity<ApiResponse<Page<GameListDTO>>> replyGameList(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "15") int item) {
		
		Page<GameListDTO> replyGameList = replyService.findGameReplyLike(ReplyType.GAME, page,item);

		ApiResponse<Page<GameListDTO>> response = ApiResponse.success(replyGameList);

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
	

}
