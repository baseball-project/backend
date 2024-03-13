package com.example.baseballprediction.domain.game.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.baseballprediction.domain.game.dto.GameVoteProjection;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.Status;

public interface GameRepository extends JpaRepository<Game, Long> {
	List<Game> findAllByStartedAtBetween(LocalDateTime startedAtStart, LocalDateTime startedAtEnd);

	// @Query(value =
	// 	"SELECT g.game_id, date_format(g.startedAt, '%Y.%m.%d') start_date, g.home_team_id, t.name home_team_name,"
	// 		+ " g.away_team_id, t2.name away_team_name, v.team_id"
	// 		+ "  FROM game g LEFT JOIN (SELECT * FROM game_vote WHERE v.member_id = :memberId) v on v.game_id = g.game_id"
	// 		+ "  INNER JOIN team t ON g.home_team_id = t.team_id"
	// 		+ "  INNER JOIN team t2 ON g.away_team_id = t2.team_id"
	// 		+ " WHERE g.started_at >= :startedAtStart and g.started_at <= :startedAtEnd", nativeQuery = true)
	// List<GameVoteProjection> findPastGameByStartedAtBetween(Long memberId, LocalDateTime staredAtStart,
	// 	LocalDateTime startedAtEnd);

	@Query(value =
		"SELECT g.game_id gameId, date_format(g.started_at, '%Y.%m.%d') startDate, g.home_team_id homeTeamId, t.name homeTeamName,"
			+ " g.away_team_id awayTeamId, t2.name awayTeamName, v.team_id voteTeamId"
			+ "  FROM game g LEFT JOIN (SELECT * FROM game_vote WHERE member_id = :memberId) v on v.game_id = g.game_id"
			+ "  INNER JOIN team t ON g.home_team_id = t.team_id"
			+ "  INNER JOIN team t2 ON g.away_team_id = t2.team_id"
			+ " WHERE g.started_at >= :startedAtStart and g.started_at <= :startedAtEnd", nativeQuery = true)
	List<GameVoteProjection> findPastGameByStartedAtBetween(Long memberId, LocalDateTime startedAtStart,
		LocalDateTime startedAtEnd);

	Optional<Game> findByHomeTeamAndAwayTeamAndStartedAt(Team homeTeam, Team awayTeam, LocalDateTime startedAt);
	
	
	@Query("SELECT g.id FROM Game g WHERE DATE(g.startedAt) = CURRENT_DATE AND (g.status = 'READY' OR g.status = 'PROGRESS')")
	List<Long> findGameIdAndStatus();
	
	@Query("SELECT g.id FROM Game g WHERE g.status = :status")
	List<Long> findGameIdsByStatus(@Param("status") Status status);
}
