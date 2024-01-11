package com.example.baseballprediction.domain.team.service;

import static com.example.baseballprediction.domain.team.dto.TeamResponse.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
	private final TeamRepository teamRepository;

	public List<TeamsDTO> findTeams() {
		List<Team> teams = teamRepository.findAll();

		return teams.stream().map(m -> new TeamsDTO(m)).toList();
	}
}
