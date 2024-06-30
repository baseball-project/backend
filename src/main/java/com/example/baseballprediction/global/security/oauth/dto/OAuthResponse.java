package com.example.baseballprediction.global.security.oauth.dto;

import com.example.baseballprediction.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class OAuthResponse {
	@Getter
	@AllArgsConstructor
	public static class LoginDTO {
		private boolean isNewMember;
		private String profileImageUrl;
		private String nickname;
		private String teamName;

		private String accessToken;

		public LoginDTO(Member member, String accessToken) {
			this.isNewMember = member.isNewMember();
			this.profileImageUrl = member.getProfileImageUrl();
			this.nickname = member.getNickname();
			this.teamName = member.getTeam() == null ? null : member.getTeam().getName();
			this.accessToken = accessToken;
		}
	}
}
