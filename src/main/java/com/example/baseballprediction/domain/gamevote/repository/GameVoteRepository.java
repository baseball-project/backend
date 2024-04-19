package com.example.baseballprediction.domain.gamevote.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.baseballprediction.domain.gamevote.dto.GameVoteRatioDTO;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;

public interface GameVoteRepository extends JpaRepository<GameVote, Long> {
	
	GameVote findByMemberIdAndGameId(Long memberId, Long gameId);
	
	@Query(
			value =
			"SELECT "
			+ " ifnull(concat(round((( (select count(team_id) from game_vote gv2 where gv2.team_id =:homeTeamid and game_id =:gameId )  / count(game_id)) * 100 ))),0) as homeTeamVoteRatio ,"
			+ " ifnull(concat(round((( (select count(team_id) from game_vote gv2 where gv2.team_id =:awayTeamid and game_id =:gameId )  / count(game_id)) * 100 ))),0) as awayTeamVoteRatio "
			+ " from game_vote gv "
			+ " where game_id =:gameId", nativeQuery = true)
	Optional<GameVoteRatioDTO> findVoteRatio(@Param("homeTeamid") int homeTeamid, @Param("awayTeamid") int awayTeamid, @Param("gameId") Long gameId);
	
	boolean existsByGameIdAndMemberId(Long gameId, Long memberId);
}