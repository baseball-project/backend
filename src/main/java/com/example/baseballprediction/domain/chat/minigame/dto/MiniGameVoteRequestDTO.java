package com.example.baseballprediction.domain.chat.minigame.dto;

import lombok.Getter;

public class MiniGameVoteRequestDTO {

	@Getter
    public static class VoteCreation {
        private Long gameId;
        private String question;
        private String option1;
        private String option2;
        
    }
	
	@Getter
    public static class VoteAction {
        private Long miniGameId;
        private int option;
    }
	
	@Getter
	public static class ResultRatioDTO {
	    private Long miniGameId;
	}
	
}
