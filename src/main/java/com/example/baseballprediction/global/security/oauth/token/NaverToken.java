package com.example.baseballprediction.global.security.oauth.token;

import lombok.Getter;

@Getter
public class NaverToken {
	private String accessToken;
	private String tokenType;
	private String refreshToken;
	private int expiresIn;
}
