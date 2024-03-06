package com.example.baseballprediction.domain.chat.minigamevote.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteResultDTO;
import com.example.baseballprediction.domain.chat.minigamevote.entity.MiniGameVote;

public interface  MiniGameVoteRepository extends JpaRepository<MiniGameVote, Long> {

	 Optional<MiniGameVote> findByMiniGameIdAndMemberNickname(Long miniGameId, String nickname);
	 
	 
	 @Query(value = "SELECT " +
             "IFNULL(ROUND((SUM(CASE WHEN vote_option = 1 THEN 1 ELSE 0 END) / COUNT(*)) * 100), 0) AS option1VoteRatio, " +
             "IFNULL(ROUND((SUM(CASE WHEN vote_option = 2 THEN 1 ELSE 0 END) / COUNT(*)) * 100), 0) AS option2VoteRatio, " +
             "mg.creator_member_id AS creatorMemberId " +
             "FROM mini_game_vote mgv " +
             "LEFT JOIN mini_game mg ON mg.mini_game_id = mgv.mini_game_id " +
             "WHERE mgv.mini_game_id = :miniGameId", nativeQuery = true)
	 MiniGameVoteResultDTO findVoteRatiosAndCreatorMemberId(@Param("miniGameId") Long miniGameId);
	 
	 
}
