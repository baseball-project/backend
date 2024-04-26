package com.example.baseballprediction.domain.member.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.service.LoginService;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import com.example.baseballprediction.global.security.oauth.service.OAuth2MemberService;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;
	private final OAuth2MemberService oAuth2MemberService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Object>> login(@RequestBody MemberRequest.LoginDTO loginDTO) {
		Map<String, Object> response = loginService.login(loginDTO.getUsername(), loginDTO.getPassword());

		ApiResponse<Object> apiResponse = ApiResponse.success(response.get("body"));

		return ResponseEntity.ok().header(JwtTokenProvider.HEADER, (String)response.get("token")).body(apiResponse);
	}

	@GetMapping("/logout")
	public ResponseEntity<ApiResponse> logout(@RequestHeader(JwtTokenProvider.HEADER) String authorization) {
		String token = authorization.split(" ")[1];

		loginService.logout(authorization.split(" ")[1]);

		ApiResponse response = ApiResponse.successWithNoData();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/login/oauth2/code/{registrationId}")
	public ResponseEntity<ApiResponse<Object>> loginWithOAuth2(@PathVariable String registrationId,
		@RequestParam String code) {
		Map<String, Object> response = oAuth2MemberService.login(code,
			SocialType.valueOf(registrationId.toUpperCase()));

		ApiResponse<Object> apiResponse = ApiResponse.success(response.get("body"));
		return ResponseEntity.ok().header(JwtTokenProvider.HEADER, (String)response.get("token")).body(apiResponse);
	}
}
