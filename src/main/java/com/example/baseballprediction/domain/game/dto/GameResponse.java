package com.example.baseballprediction.domain.game.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRatioDTO;
import com.example.baseballprediction.domain.team.entity.Team;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GameResponse {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GameDtoDaily {

		private Long gameId;

		private TeamDailyDTO homeTeam;

		private TeamDailyDTO awayTeam;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		@LastModifiedDate
		private LocalDateTime gameTime;

		private String status;

		public GameDtoDaily(Game game, Team homeTeam, Team awayTeam,GameVoteRatioDTO gameVoteRatioDTO) {
			this.gameId = game.getId();
			this.homeTeam = new TeamDailyDTO(homeTeam, game.getHomeTeamScore(),gameVoteRatioDTO.getHomeTeamVoteRatio(),homeTeam.getId());
			this.awayTeam = new TeamDailyDTO(awayTeam, game.getAwayTeamScore(),gameVoteRatioDTO.getAwayTeamVoteRatio(),awayTeam.getId());
			this.gameTime = game.getStartedAt();
			this.status = game.getStatus().toString();
		}

	}

	@Getter
	public static class TeamDailyDTO {

		private String teamName;
		private String teamShortName;
		private int score;
		private int voteRatio;
		private int id;
		

		public TeamDailyDTO(Team team,int score, int voteRatio, int id) {
			this.teamName = team.getName();
			this.teamShortName = team.getShortName();
			this.score = score;
			this.voteRatio = voteRatio;
			this.id = id;
		}

	}

}
	
