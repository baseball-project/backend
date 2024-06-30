package com.example.baseballprediction.global.security.jwt.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.global.security.jwt.service.JwtService;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class JwtController {
	private final JwtService jwtService;

	@PostMapping("/token")
	public ResponseEntity<ApiResponse<String>> createNewAccessToken(
		@CookieValue("refreshToken") String refreshTokenId) {
		Map<String, String> response = jwtService.createNewToken(refreshTokenId);

		ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.get("refreshTokenId"))
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(60 * 60 * 24)
			.sameSite("None")
			.domain("playdot.vercel.app")
			.build();

		ApiResponse<String> apiResponse = ApiResponse.success(response.get("accessToken"));

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, responseCookie.toString())
			.body(apiResponse);
	}
}
