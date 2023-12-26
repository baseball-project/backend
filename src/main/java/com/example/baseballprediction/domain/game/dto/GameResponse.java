package com.example.baseballprediction.kbo.dto;

import java.time.LocalDateTime;
import org.springframework.data.annotation.LastModifiedDate;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.team.entity.Team;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class KboResponse {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class KboDtoDaily{
		
		private Long gameId;
		
		private TeamDailyDTO homeTeam;
		
		private TeamDailyDTO awayTeam;
		
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		@LastModifiedDate
		private LocalDateTime gameTime;
		
		private String status;
		
		public KboDtoDaily(Game game, Team homeTeam, Team awayTeam) {
			this.gameId = game.getId();
			this.homeTeam = new TeamDailyDTO(homeTeam);
			this.awayTeam = new TeamDailyDTO(awayTeam);
			this.gameTime = game.getStartedAt();
			this.status = game.getStatus().toString();
		}
		
	}	
		
	@Getter	
	public static class TeamDailyDTO{
		
		private String teamName;
		
		private String teamLogoUrl;
		
		public TeamDailyDTO(Team team) {
			this.teamName = team.getShortName();
			this.teamLogoUrl = team.getLogoUrl();
		}
	
	}
	
}
	
