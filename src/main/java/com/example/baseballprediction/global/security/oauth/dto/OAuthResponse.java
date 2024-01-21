package com.example.baseballprediction.global.security.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class OAuthResponse {
	@Getter
	@AllArgsConstructor
	public static class LoginDTO {
		private boolean isNewMember;
	}
}
