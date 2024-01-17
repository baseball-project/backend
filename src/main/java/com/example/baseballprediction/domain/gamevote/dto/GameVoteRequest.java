package com.example.baseballprediction.domain.gamevote.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class GameVoteRequest {

	@Getter
	@NoArgsConstructor
	public static class GameVoteRequestDTO {
	    private Integer teamId;
	}
}