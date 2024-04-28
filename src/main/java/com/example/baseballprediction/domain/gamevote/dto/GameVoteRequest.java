package com.example.baseballprediction.domain.gamevote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GameVoteRequest {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GameVoteRequestDTO {

		@NotNull(message = "teamId는 필수입니다.")
		private Integer teamId;
	}
}