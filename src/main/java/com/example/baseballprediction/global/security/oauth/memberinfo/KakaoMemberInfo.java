package com.example.baseballprediction.global.security.oauth.memberinfo;

import java.util.Map;

import com.example.baseballprediction.global.constant.SocialType;

public class KakaoMemberInfo implements OAuth2MemberInfo {
	private Map<String, Object> attributes;

	private Map<String, Object> kakaoAccountAttributes;

	private Map<String, Object> profileAttributes;

	public KakaoMemberInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		this.kakaoAccountAttributes = (Map<String, Object>)attributes.get("kakao_account");
		this.profileAttributes = (Map<String, Object>)kakaoAccountAttributes.get("profile");
	}

	@Override

	public String getProviderId() {
		return attributes.get("id").toString();
	}

	@Override
	public SocialType getSocialType() {
		return SocialType.KAKAO;
	}

	@Override
	public String getEmail() {
		return kakaoAccountAttributes.get("email").toString();
	}

}
