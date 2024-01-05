package com.example.baseballprediction.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
		@NotBlank(message = "닉네임을 입력해주세요.")
		@Size(max = 20, message = "닉네임은 20자 이하로 설정할 수 있습니다.")
		private String nickname;

		@Size(max = 100, message = "한마디는 100자 이하로 설정할 수 있습니다.")
		private String comment;
	}
}
