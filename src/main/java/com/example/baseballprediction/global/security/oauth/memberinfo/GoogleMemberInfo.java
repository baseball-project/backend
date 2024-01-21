package com.example.baseballprediction.global.security.oauth.memberinfo;

import java.util.Map;

import com.example.baseballprediction.global.constant.SocialType;

public class GoogleMemberInfo implements OAuth2MemberInfo {
	private Map<String, Object> attributes;

	public GoogleMemberInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProviderId() {
		return attributes.get("sub").toString();
	}

	@Override
	public SocialType getSocialType() {
		return SocialType.GOOGLE;
	}

	@Override
	public String getEmail() {
		return attributes.get("email").toString();
	}
}
