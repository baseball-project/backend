package com.example.baseballprediction.domain.member.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.member.dto.FairyProjection;
import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.service.HistoryService;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/history")
public class HistoryController {
	private final HistoryService historyService;

	@GetMapping("/fairy-statistics")
	public ResponseEntity<ApiResponse<List<FairyProjection>>> fairyStatisticList(
		@AuthenticationPrincipal MemberDetails memberDetails) {
		List<FairyProjection> fairyProjections = historyService.findFairyStatistics(memberDetails.getMember().getId());

		ApiResponse<List<FairyProjection>> response = ApiResponse.success(fairyProjections);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/votes")
	public ResponseEntity<ApiResponse<Page<MemberResponse.FairyHistoryDTO>>> fairyHistoryList(
		@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int list) {

		Page<MemberResponse.FairyHistoryDTO> fairyHistories = historyService.findFairyHistories(
			memberDetails.getMember().getId(), page,
			list);

		ApiResponse<Page<MemberResponse.FairyHistoryDTO>> response = ApiResponse.success(fairyHistories);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/gifts")
	public ResponseEntity<ApiResponse<?>> giftHistoryList(@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int list) {
		Page<MemberResponse.GiftHistoryDTO> giftHistories = historyService.findGiftHistories(memberDetails.getMember()
			.getId(), page, list);

		ApiResponse<Page<MemberResponse.GiftHistoryDTO>> response = ApiResponse.success(giftHistories);

		return ResponseEntity.ok(response);
	}
}
