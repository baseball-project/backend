package com.example.baseballprediction.domain.gamevote.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRequest.GameVoteRequestDTO;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameVoteService {
	private final MemberRepository memberRepository;
	private final GameVoteRepository gameVoteRepository;
	private final TeamRepository teamRepository;
	private final GameRepository gameRepository;
	
	@Transactional
	public void addGameVote(String username,Long gameId,GameVoteRequestDTO gameVoteRequestDTO) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		Team team = teamRepository.findById(gameVoteRequestDTO.getTeamId()).orElseThrow();
		Game game = gameRepository.findById(gameId).orElseThrow();
		GameVote gameVoteCheck = gameVoteRepository.findByMemberIdAndGameId(member.getId(), gameId);
		
		if(gameVoteCheck != null) {
			throw new BusinessException(ErrorCode.VOTING_ALREADY_COMPLETED);
		}
		
			GameVote gameVote = GameVote.builder()
					.member(member)
					.team(team)
					.game(game)
					.build();
			
			gameVoteRepository.save(gameVote);
	}
	
	@Transactional
	public void modifyGameVote(String username,Long gameId,GameVoteRequestDTO gameVoteRequestDTO) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		Team team = teamRepository.findById(gameVoteRequestDTO.getTeamId()).orElseThrow();
		GameVote gameVote = gameVoteRepository.findByMemberIdAndGameId(member.getId(), gameId);
		
		if(gameVote == null) {
			throw new BusinessException(ErrorCode.VOTING_DATA_NOT_FOUND);
		}
		
		gameVote.modifyTeam(team);
	}
	
	
	@Transactional
	public void removeGameVote(Long gameId, String username) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		GameVote gameVote = gameVoteRepository.findByMemberIdAndGameId(member.getId(), gameId);
		
		if(gameVote == null) {
			throw new BusinessException(ErrorCode.VOTING_DATA_NOT_FOUND);
		}
		
		gameVoteRepository.delete(gameVote);
	}
}
