package com.example.baseballprediction.domain.member.controller;

import static com.example.baseballprediction.domain.member.dto.MemberRequest.*;
import static com.example.baseballprediction.domain.member.dto.MemberResponse.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.baseballprediction.domain.member.dto.FairyProjection;
import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.dto.ProfileProjection;
import com.example.baseballprediction.domain.member.service.MemberService;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import com.example.baseballprediction.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Object>> login(@RequestBody MemberRequest.LoginDTO loginDTO) {
		Map<String, Object> response = memberService.login(loginDTO.getUsername(), loginDTO.getPassword());

		ApiResponse<Object> apiResponse = ApiResponse.success(response.get("body"));

		return ResponseEntity.ok().header(JwtTokenProvider.HEADER, (String)response.get("token")).body(apiResponse);
	}

	@GetMapping("/logout")
	public ResponseEntity<ApiResponse> logout(@RequestHeader(JwtTokenProvider.HEADER) String authorization) {
		String token = authorization.split(" ")[1];

		memberService.logout(authorization.split(" ")[1]);

		ApiResponse response = ApiResponse.successWithNoData();
		return ResponseEntity.ok(response);
	}

	@PutMapping("/profile/team")
	public ResponseEntity<ApiResponse<?>> likeTeamModify(@RequestBody LikeTeamDTO likeTeamDTO,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		memberService.modifyLikeTeam(memberDetails.getUsername(), likeTeamDTO.getTeamId());

		return ResponseEntity.ok(ApiResponse.createSuccess());
	}

	@PutMapping("/profile/details")
	public ResponseEntity<ApiResponse<?>> detailsModify(@RequestPart("data") @Valid DetailsDTO detailsDTO,
		@RequestPart(name = "profileImage", required = false) MultipartFile profileImage,
		@AuthenticationPrincipal MemberDetails memberDetails) {

		memberService.modifyDetails(memberDetails.getUsername(), detailsDTO, profileImage);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@GetMapping("/profiles/{memberId}")
	public ResponseEntity<ApiResponse<ProfileProjection>> profileList(@PathVariable Long memberId) {
		ProfileProjection profile = memberService.findProfile(memberId);

		return ResponseEntity.ok(ApiResponse.success(profile));
	}

	@GetMapping("/profile/details")
	public ResponseEntity<ApiResponse<ProfileDTO>> detailsList(@AuthenticationPrincipal MemberDetails memberDetails) {

		ProfileDTO profile = memberService.findDetails(memberDetails.getUsername());

		ApiResponse<ProfileDTO> response = ApiResponse.success(profile);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/profile/history/fairy-statistics")
	public ResponseEntity<ApiResponse<List<FairyProjection>>> fairyStatisticList(
		@AuthenticationPrincipal MemberDetails memberDetails) {
		List<FairyProjection> fairyProjections = memberService.findFairyStatistics(memberDetails.getMember().getId());

		ApiResponse<List<FairyProjection>> response = ApiResponse.success(fairyProjections);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/profile/history/votes")
	public ResponseEntity<ApiResponse<Page<FairyHistoryDTO>>> fairyHistoryList(
		@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int list) {

		Page<FairyHistoryDTO> fairyHistories = memberService.findFairyHistories(memberDetails.getMember().getId(), page,
			list);

		ApiResponse<Page<FairyHistoryDTO>> response = ApiResponse.success(fairyHistories);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/profile/history/gifts")
	public ResponseEntity<ApiResponse<?>> giftHistoryList(@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int list) {
		Page<GiftHistoryDTO> giftHistories = memberService.findGiftHistories(memberDetails.getMember()
			.getId(), page, list);

		ApiResponse<Page<GiftHistoryDTO>> response = ApiResponse.success(giftHistories);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/profile/nickname-check")
	public ResponseEntity<ApiResponse<MemberResponse.NicknameDTO>> nicknameExistList(
		@AuthenticationPrincipal MemberDetails memberDetails,
		@Valid @RequestBody MemberRequest.NicknameDTO nicknameDTO) {
		MemberResponse.NicknameDTO responseDTO = memberService.findExistNickname(memberDetails.getMember().getId(),
			nicknameDTO.getNickname());

		ApiResponse<MemberResponse.NicknameDTO> response = ApiResponse.success(responseDTO);

		return ResponseEntity.ok(response);
	}
}
