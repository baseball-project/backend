package com.example.baseballprediction.domain.gamevote.repository;

public interface GameVoteRepositoryCustom {
	 boolean existsByGameIdAndTeamIdAndMemberId(Long gameId, int teamId, Long memberId);
}
