package com.example.baseballprediction.global.security.oauth.token;

import lombok.Getter;

@Getter
public class GoogleToken {
	private String accessToken;
	private int expiresIn;
	private String scope;
	private String tokenType;
	private String idToken;
}
