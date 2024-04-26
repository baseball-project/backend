package com.example.baseballprediction.domain.chat.minigame.dto;

import lombok.Getter;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MiniGameVoteRequestDTO {

	@Getter
    public static class VoteCreation {
		@NotNull(message = "gameId는 필수입니다.")
		private Long gameId;
		
		@NotBlank(message = "질문은 필수 입력 사항입니다.")
		private String question;
		
		@NotBlank(message = "옵션 1은 필수 입력 사항입니다.")
		private String option1;
		
		@NotBlank(message = "옵션 2는 필수 입력 사항입니다.")
		private String option2;
        
    }
	
	@Getter
    public static class VoteAction {
		@NotNull(message = "miniGameId는 필수입니다.")
		private Long miniGameId;
		
		@Range(min = 1, max = 2, message = "옵션 번호는 1 또는 2여야 합니다.")
		private int option;
    }
	
	@Getter
	public static class ResultRatioDTO {
		@NotNull(message = "miniGameId는 필수입니다.")
		private Long miniGameId;
	}
	
}
