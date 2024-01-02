package com.example.baseballprediction.domain.team.dto;

import com.example.baseballprediction.domain.team.entity.Team;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class TeamResponse {
	@Getter
	@NoArgsConstructor
	public static class TeamsDTO {
		private int teamId;
		private String teamName;

		public TeamsDTO(Team team) {
			this.teamId = team.getId();
			this.teamName = team.getName();
		}
	}
}
