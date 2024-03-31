package com.example.baseballprediction.global.security.oauth.memberinfo;

import com.example.baseballprediction.global.constant.SocialType;

import lombok.Getter;

@Getter
public class GoogleMemberInfo implements OAuth2MemberInfo {
	private String id;

	private String email;
	private String verifiedEmail;
	private String picture;

	@Override

	public String getProviderId() {
		return this.id;
	}

	@Override
	public SocialType getSocialType() {
		return SocialType.GOOGLE;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public String generateNickname() {
		return "GOOGLE_" + this.id;
	}
}
