package com.example.baseballprediction.global.security.oauth.memberinfo;

import com.example.baseballprediction.global.constant.SocialType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
public class KakaoMemberInfo implements OAuth2MemberInfo {
	private String id;
	@JsonProperty("connected_at")
	private String connectedAt;
	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	@Getter
	@Setter
	public class KakaoAccount {
		@JsonProperty("has_email")
		private Boolean hasEmail;
		@JsonProperty("email_needs_agreement")
		private Boolean emailNeedsAgreement;
		@JsonProperty("is_email_valid")
		private Boolean isEmailValid;
		@JsonProperty("is_email_verified")
		private Boolean isEmailVerified;
		private String email;
	}

	@Override
	public String getProviderId() {
		return id;
	}

	@Override
	public SocialType getSocialType() {
		return SocialType.KAKAO;
	}

	@Override
	public String getEmail() {
		return kakaoAccount.email;
	}

}
