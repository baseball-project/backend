package com.example.baseballprediction.domain.game.dto;

public interface GameVoteProjection {
	Long getGameId();

	String getStartDate();

	Integer getHomeTeamId();

	String getHomeTeamName();

	Integer getAwayTeamId();

	String getAwayTeamName();

	Integer getVoteTeamId();
}
