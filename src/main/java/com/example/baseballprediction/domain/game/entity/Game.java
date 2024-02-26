package com.example.baseballprediction.domain.game.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Game {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_team_id")
	private Team homeTeam;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_team_id")
	private Team awayTeam;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private Status status;

	@Column(nullable = false)
	private LocalDateTime startedAt;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int homeTeamScore;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int awayTeamScore;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "win_team_id", unique = false)
	private Team winTeam;

	@Builder
	public Game(Team homeTeam, Team awayTeam, Status status, LocalDateTime startedAt, int homeTeamScore,
		int awayTeamScore) {
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.status = status;
		this.startedAt = startedAt;
		this.homeTeamScore = homeTeamScore;
		this.awayTeamScore = awayTeamScore;
	}

	public void updateByScrapeData(int homeTeamScore, int awayTeamScore, Status status) {
		this.homeTeamScore = homeTeamScore;
		this.awayTeamScore = awayTeamScore;
		this.status = status;
	}
}
