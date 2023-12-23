package com.example.baseballprediction.domain.monthlyfairy.controller;

import static com.example.baseballprediction.domain.monthlyfairy.dto.MonthlyFairyResponse.*;
import static com.example.baseballprediction.domain.reply.dto.ReplyResponse.*;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.monthlyfairy.service.MonthlyFairyService;
import com.example.baseballprediction.domain.reply.dto.ReplyRequest;
import com.example.baseballprediction.domain.reply.service.ReplyService;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.security.auth.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class MonthlyFairyController {
	private final MonthlyFairyService monthlyFairyService;
	private final ReplyService replyService;

	@GetMapping("")
	public ResponseEntity<ApiResponse<StatisticsDTO>> monthlyFairyList() {
		StatisticsDTO statistics = monthlyFairyService.findStatistics();

		ApiResponse<StatisticsDTO> response = ApiResponse.success(statistics);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/replies")
	public ResponseEntity<ApiResponse<Page<ReplyDTO>>> replyList(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "15") int item) {
		if (page < 0)
			throw new RuntimeException("페이지 번호를 확인해주세요.");

		page = page == 0 ? page : page - 1;

		Page<ReplyDTO> replies = replyService.findRepliesByType(ReplyType.FAIRY, page, item);

		ApiResponse<Page<ReplyDTO>> response = ApiResponse.success(replies);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/reply")
	public ResponseEntity<ApiResponse> replyAdd(@AuthenticationPrincipal MemberDetails memberDetails, @RequestBody
	ReplyRequest.ReplyDTO replyDTO) {
		replyService.addReply(ReplyType.FAIRY, memberDetails.getUsername(), replyDTO.getContent());

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess());
	}
}
