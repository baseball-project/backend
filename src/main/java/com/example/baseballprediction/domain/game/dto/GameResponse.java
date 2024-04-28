package com.example.baseballprediction.domain.game.dto;

import java.time.LocalDateTime;
import java.util.Objects;

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

		public GameDtoDaily(Game game, Team homeTeam, Team awayTeam, GameVoteRatioDTO gameVoteRatioDTO,
			boolean homeTeamHasVoted, boolean awayTeamHasVoted) {
			this.gameId = game.getId();
			this.homeTeam = new TeamDailyDTO(homeTeam, game.getHomeTeamScore(), gameVoteRatioDTO.getHomeTeamVoteRatio(),
				homeTeam.getId(), homeTeamHasVoted);
			this.awayTeam = new TeamDailyDTO(awayTeam, game.getAwayTeamScore(), gameVoteRatioDTO.getAwayTeamVoteRatio(),
				awayTeam.getId(), awayTeamHasVoted);
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
		private boolean hasVote;

		public TeamDailyDTO(Team team, int score, int voteRatio, int id, boolean hasVote) {
			this.teamName = team.getName();
			this.teamShortName = team.getShortName();
			this.score = score;
			this.voteRatio = voteRatio;
			this.id = id;
			this.hasVote = hasVote;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			TeamDailyDTO that = (TeamDailyDTO)o;
			return score == that.score && voteRatio == that.voteRatio && id == that.id && hasVote == that.hasVote
				&& Objects.equals(teamName, that.teamName) && Objects.equals(teamShortName,
				that.teamShortName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(teamName, teamShortName, score, voteRatio, id, hasVote);
		}
	}

	@Getter
	@NoArgsConstructor
	public static class PastGameDTO {
		private Long gameId;
		private PastGameTeamDTO homeTeam;
		private PastGameTeamDTO awayTeam;
		private String gameDate;
		private Integer voteTeamId;

		public PastGameDTO(GameVoteProjection gameVoteProjection, GameVoteRatioDTO gameVoteRatioDTO) {
			this.gameId = gameVoteProjection.getGameId();
			this.homeTeam = new PastGameTeamDTO(gameVoteProjection.getHomeTeamId(),
				gameVoteProjection.getHomeTeamName(), gameVoteRatioDTO.getHomeTeamVoteRatio());
			this.awayTeam = new PastGameTeamDTO(gameVoteProjection.getAwayTeamId(),
				gameVoteProjection.getAwayTeamName(), gameVoteRatioDTO.getAwayTeamVoteRatio());
			this.gameDate = gameVoteProjection.getStartDate();
			this.voteTeamId = gameVoteProjection.getVoteTeamId();
		}
	}

	@Getter
	@AllArgsConstructor
	public static class PastGameTeamDTO {
		private int id;
		private String teamName;
		private int voteRatio;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			PastGameTeamDTO that = (PastGameTeamDTO)o;
			return id == that.id && voteRatio == that.voteRatio && Objects.equals(teamName, that.teamName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, teamName, voteRatio);
		}
	}

}
	
