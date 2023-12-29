package com.example.baseballprediction.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {
	@Getter
	@NoArgsConstructor
	public static class LoginDTO {
		private String username;
		private String password;
	}

	@Getter
	@NoArgsConstructor
	public static class LikeTeamDTO {
		private Integer teamId;
	}

	@Getter
	@NoArgsConstructor
	public static class DetailsDTO {
		private String nickname;
		private String comment;
	}
}
