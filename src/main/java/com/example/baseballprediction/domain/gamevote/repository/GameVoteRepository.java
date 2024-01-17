package com.example.baseballprediction.domain.gamevote.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.gamevote.entity.GameVote;

public interface GameVoteRepository extends JpaRepository<GameVote, Long> {
	
	GameVote findByMemberIdAndGameId(Long memberId, Long gameId);
	
}