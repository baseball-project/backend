package com.example.baseballprediction.global.security.oauth.memberinfo;

import java.util.Map;

import com.example.baseballprediction.global.constant.SocialType;

public class NaverMemberInfo implements OAuth2MemberInfo {
	private Map<String, Object> attributes;

	public NaverMemberInfo(Map<String, Object> attributes) {
		this.attributes = (Map<String, Object>)attributes.get("response");
	}

	@Override
	public String getProviderId() {
		return attributes.get("id").toString();
	}

	@Override
	public SocialType getSocialType() {
		return SocialType.NAVER;
	}

	@Override
	public String getEmail() {
		return attributes.get("email").toString();
	}
}
