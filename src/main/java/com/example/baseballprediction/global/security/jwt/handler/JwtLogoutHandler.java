package com.example.baseballprediction.global.security.jwt.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.JwtException;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtLogoutHandler implements LogoutHandler {
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String token = request.getHeader("Authorization");
		if (token == null) {
			throw new JwtException(ErrorCode.JWT_INVALID);
		}

		token = token.split(" ")[1];

		JwtTokenProvider.expireToken(token);
	}
}
