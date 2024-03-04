package com.example.baseballprediction.domain.reply.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.reply.dto.ReplyReportRequest;
import com.example.baseballprediction.domain.reply.dto.ReplyReportResponse;
import com.example.baseballprediction.domain.reply.service.ReplyService;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {
	private final ReplyService replyService;

	@DeleteMapping("/{replyId}")
	public ResponseEntity<ApiResponse> replyRemove(@AuthenticationPrincipal MemberDetails memberDetails,
		@PathVariable Long replyId) {
		replyService.deleteReply(replyId, memberDetails.getUsername());

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@GetMapping("/{replyId}/report")
	public ResponseEntity<ApiResponse> replyReportList(@PathVariable Long replyId) {
		List<ReplyReportResponse.ListDTO> replyReports = replyService.findReportTypes();

		ApiResponse<List<ReplyReportResponse.ListDTO>> response = ApiResponse.success(replyReports);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{replyId}/report")
	public ResponseEntity<ApiResponse> replyReportAdd(@PathVariable Long replyId,
		@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody ReplyReportRequest.ReportDTO reportDTO) {
		replyService.addReplyReport(replyId, memberDetails.getUsername(), reportDTO.getReportType());

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}
}
