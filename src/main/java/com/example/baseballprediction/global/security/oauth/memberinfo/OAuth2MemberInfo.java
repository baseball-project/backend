package com.example.baseballprediction.global.security.oauth.memberinfo;

import com.example.baseballprediction.global.constant.SocialType;

public interface OAuth2MemberInfo {
	String getProviderId();

	SocialType getSocialType();

	String getEmail();

	String generateNickname();
}
