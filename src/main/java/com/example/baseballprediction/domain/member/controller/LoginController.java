package com.example.baseballprediction.domain.member.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.service.LoginService;
import com.example.baseballprediction.global.constant.SocialType;
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

		ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.get("refreshTokenId").toString())
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(60 * 60 * 24)
			.sameSite("None")
			.domain("playdot.vercel.app")
			.build();

		ApiResponse<Object> apiResponse = ApiResponse.success(response.get("body"));

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
			.body(apiResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse> logout(@CookieValue("refreshToken") String refreshToken) {
		loginService.logout(refreshToken);

		ApiResponse response = ApiResponse.successWithNoData();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/login/oauth2/code/{registrationId}")
	public ResponseEntity<ApiResponse<Object>> loginWithOAuth2(@PathVariable String registrationId,
		@RequestParam String code) {
		Map<String, Object> response = oAuth2MemberService.login(code,
			SocialType.valueOf(registrationId.toUpperCase()));

		ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.get("refreshTokenId").toString())
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(60 * 60 * 24)
			.sameSite("None")
			.domain("localhost")
			.build();

		ApiResponse<Object> apiResponse = ApiResponse.success(response.get("body"));

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
			.body(apiResponse);
	}
}
