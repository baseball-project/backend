package com.example.baseballprediction.domain.gamevote.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.baseballprediction.domain.gamevote.dto.GameVoteRatioDTO;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;

public interface GameVoteRepository extends JpaRepository<GameVote, Long> {

	GameVote findByMemberIdAndGameId(Long memberId, Long gameId);

	@Query(value =
		"SELECT " +
			"COALESCE(ROUND(((SELECT COUNT(team_id) FROM game_vote gv2 WHERE gv2.team_id = :homeTeamid AND game_id = :gameId) / CASE WHEN COUNT(game_id) = 0 THEN 1 ELSE COUNT(game_id) END) * 100) , 0) AS homeTeamVoteRatio, "
			+
			"COALESCE(ROUND(((SELECT COUNT(team_id) FROM game_vote gv2 WHERE gv2.team_id = :awayTeamid AND game_id = :gameId) / CASE WHEN COUNT(game_id) = 0 THEN 1 ELSE COUNT(game_id) END) * 100), 0) AS awayTeamVoteRatio "
			+
			"FROM game_vote gv " +
			"WHERE game_id = :gameId", nativeQuery = true)
	Optional<GameVoteRatioDTO> findVoteRatio(@Param("homeTeamid") int homeTeamid, @Param("awayTeamid") int awayTeamid,
		@Param("gameId") Long gameId);

	boolean existsByGameIdAndMemberId(Long gameId, Long memberId);
}