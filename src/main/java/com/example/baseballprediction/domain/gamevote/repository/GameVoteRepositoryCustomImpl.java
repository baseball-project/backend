package com.example.baseballprediction.domain.gamevote.repository;

import org.springframework.stereotype.Repository;

import com.example.baseballprediction.domain.gamevote.entity.QGameVote;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GameVoteRepositoryCustomImpl implements GameVoteRepositoryCustom {
	
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsByGameIdAndTeamIdAndMemberId(Long gameId, int teamId, Long memberId) {
		
		QGameVote qGameVote = QGameVote.gameVote;
		
		return	queryFactory.selectFrom(qGameVote)
					.where(qGameVote.game.id.eq(gameId),
							qGameVote.team.id.eq(teamId),
							qGameVote.member.id.eq(memberId)
							).fetchOne() != null;
	}

}
