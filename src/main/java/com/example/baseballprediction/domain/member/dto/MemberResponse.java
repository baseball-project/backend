package com.example.baseballprediction.domain.member.dto;

import com.example.baseballprediction.domain.member.entity.Member;

import lombok.Getter;

public class MemberResponse {
	@Getter
	public static class ProfileDTO {
		private String nickname;
		private String profileImageUrl;
		private int level;
		private int token;
		private String teamName;
		private String comment;

		public ProfileDTO(Member member) {
			this.nickname = member.getNickname();
			this.profileImageUrl = member.getProfileImageUrl();
			this.level = member.getLevel();
			this.token = member.getToken();
			this.teamName = member.getTeam().getName();
			this.comment = member.getComment();
		}
	}
}
