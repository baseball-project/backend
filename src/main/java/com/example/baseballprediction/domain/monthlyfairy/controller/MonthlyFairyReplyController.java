package com.example.baseballprediction.domain.monthlyfairy.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.reply.dto.ReplyRequest;
import com.example.baseballprediction.domain.reply.dto.ReplyResponse;
import com.example.baseballprediction.domain.reply.service.ReplyService;
import com.example.baseballprediction.domain.replylike.service.ReplyLikeService;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class MonthlyFairyReplyController {
	private final ReplyService replyService;
	private final ReplyLikeService replyLikeService;

	@GetMapping("/replies")
	public ResponseEntity<ApiResponse<Page<ReplyResponse.ReplyDTO>>> replyList(
		@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "15") int item) {
		if (page < 0)
			throw new BusinessException(ErrorCode.PAGE_NO_BAD_REQUEST);

		page = page == 0 ? page : page - 1;

		Page<ReplyResponse.ReplyDTO> replies = replyService.findRepliesByType(ReplyType.FAIRY, page, item,
			memberDetails.getUsername());

		ApiResponse<Page<ReplyResponse.ReplyDTO>> response = ApiResponse.success(replies);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/reply")
	public ResponseEntity<ApiResponse> replyAdd(@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody
	@Valid ReplyRequest.ReplyDTO replyDTO) {
		replyService.addReply(ReplyType.FAIRY, memberDetails.getUsername(), replyDTO.getContent());

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}

	@DeleteMapping("/replies/{replyId}")
	public ResponseEntity<ApiResponse> replyRemove(@AuthenticationPrincipal MemberDetails memberDetails,
		@PathVariable Long replyId) {
		replyService.deleteReply(replyId, memberDetails.getUsername());

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@PostMapping("/replies/{replyId}/like")
	public ResponseEntity<ApiResponse> replyLikeAdd(@AuthenticationPrincipal MemberDetails memberDetails,
		@PathVariable Long replyId) {

		replyLikeService.saveReplyLike(memberDetails.getUsername(), replyId);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@DeleteMapping("/replies/{replyId}/like")
	public ResponseEntity<ApiResponse> replyLikeCancel(@AuthenticationPrincipal MemberDetails memberDetails,
		@PathVariable Long replyId) {
		replyLikeService.deleteReplyLike(memberDetails.getUsername(), replyId);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@GetMapping("/replies/{replyId}/sub")
	public ResponseEntity<ApiResponse<List<ReplyResponse.ReplyDTO>>> replySubList(@PathVariable Long replyId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		List<ReplyResponse.ReplyDTO> replies = replyService.findSubReplies(replyId, memberDetails.getUsername());

		ApiResponse<List<ReplyResponse.ReplyDTO>> response = ApiResponse.success(replies);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/replies/{replyId}/sub")
	public ResponseEntity<ApiResponse> replySubAdd(@PathVariable Long replyId,
		@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody
	@Valid ReplyRequest.ReplyDTO replyDTO) {
		replyService.addSubReply(replyId, ReplyType.FAIRY, memberDetails.getUsername(), replyDTO.getContent());

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}
}
