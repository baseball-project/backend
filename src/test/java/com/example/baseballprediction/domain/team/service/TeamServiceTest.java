package com.example.baseballprediction.domain.team.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.team.dto.TeamResponse;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;

@ActiveProfiles("test")
@SpringBootTest
class TeamServiceTest {
	@Autowired
	private TeamService teamService;

	@Autowired
	private TeamRepository teamRepository;

	@DisplayName("팀 목록을 조회한다.")
	@Test
	void findTeams() {
		///given
		Team team1 = createTeam("한화 이글스", "한화");
		Team team2 = createTeam("LG 트윈스", "LG");
		Team team3 = createTeam("두산 베어스", "두산");

		teamRepository.saveAll(List.of(team1, team2, team3));

		//when
		List<TeamResponse.TeamsDTO> result = teamService.findTeams();

		//then
		assertThat(result).hasSize(3)
			.extracting("teamName")
			.containsExactlyInAnyOrder(
				"한화 이글스",
				"LG 트윈스",
				"두산 베어스"
			);
	}

	private Team createTeam(String name, String shortName) {
		return Team.builder()
			.name(name)
			.shortName(shortName)
			.build();
	}
}