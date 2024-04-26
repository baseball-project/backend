package com.example.baseballprediction.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.dto.ProfileProjection;
import com.example.baseballprediction.domain.member.service.ProfileService;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {
	private final ProfileService profileService;

	@PutMapping("/profile/team")
	public ResponseEntity<ApiResponse<?>> likeTeamModify(@RequestBody MemberRequest.LikeTeamDTO likeTeamDTO,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		profileService.modifyLikeTeam(memberDetails.getUsername(), likeTeamDTO.getTeamId());

		return ResponseEntity.ok(ApiResponse.createSuccess());
	}

	@PutMapping("/profile/details")
	public ResponseEntity<ApiResponse<?>> detailsModify(@RequestPart("data") @Valid MemberRequest.DetailsDTO detailsDTO,
		@RequestPart(name = "profileImage", required = false) MultipartFile profileImage,
		@AuthenticationPrincipal MemberDetails memberDetails) {

		profileService.modifyDetails(memberDetails.getUsername(), detailsDTO, profileImage);

		return ResponseEntity.ok(ApiResponse.successWithNoData());
	}

	@GetMapping("/profiles")
	public ResponseEntity<ApiResponse<ProfileProjection>> profileList(@RequestParam String nickname) {
		ProfileProjection profile = profileService.findProfile(nickname);

		return ResponseEntity.ok(ApiResponse.success(profile));
	}

	@GetMapping("/profile/details")
	public ResponseEntity<ApiResponse<MemberResponse.ProfileDTO>> detailsList(
		@AuthenticationPrincipal MemberDetails memberDetails) {

		MemberResponse.ProfileDTO profile = profileService.findDetails(memberDetails.getUsername());

		ApiResponse<MemberResponse.ProfileDTO> response = ApiResponse.success(profile);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/profile/nickname-check")
	public ResponseEntity<ApiResponse<MemberResponse.NicknameDTO>> nicknameExistList(
		@AuthenticationPrincipal MemberDetails memberDetails,
		@RequestParam String nickname) {
		if (nickname.length() > 20) {
			throw new BusinessException(ErrorCode.MEMBER_NICKNAME_LENGTH);
		}

		if (nickname.isEmpty()) {
			throw new BusinessException(ErrorCode.MEMBER_NICKNAME_NULL);
		}
		MemberResponse.NicknameDTO responseDTO = profileService.findExistNickname(memberDetails.getMember().getId(),
			nickname);

		ApiResponse<MemberResponse.NicknameDTO> response = ApiResponse.success(responseDTO);

		return ResponseEntity.ok(response);
	}
}
