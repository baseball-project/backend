package com.example.baseballprediction.global.security.oauth.memberinfo;

import com.example.baseballprediction.global.constant.SocialType;

import lombok.Getter;

@Getter
public class NaverMemberInfo implements OAuth2MemberInfo {

	private String resultcode;
	private String message;
	private Response response;

	@Getter
	static class Response {
		private String id;
		private String email;
	}

	@Override
	public String getProviderId() {
		return response.id;
	}

	@Override
	public SocialType getSocialType() {
		return SocialType.NAVER;
	}

	@Override
	public String getEmail() {
		return response.email;
	}

	@Override
	public String generateNickname() {
		return "NAVER_" + response.id;
	}
}
