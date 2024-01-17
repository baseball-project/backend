package com.example.baseballprediction.domain.gamevote.service;

import org.springframework.stereotype.Service;

import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRequest.GameVoteRequestDTO;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GameVoteService {
	private final MemberRepository memberRepository;
	private final GameVoteRepository gameVoteRepository;
	private final TeamRepository teamRepository;
	private final GameRepository gameRepository;
	
	public void addGameVote(String username,Long gameId,GameVoteRequestDTO gameVoteRequestDTO) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		Team team = teamRepository.findById(gameVoteRequestDTO.getTeamId()).orElseThrow();
		Game game = gameRepository.findById(gameId).orElseThrow();
		GameVote gameVoteCheck = gameVoteRepository.findByMemberIdAndGameId(member.getId(), gameId);
		
		if(gameVoteCheck != null) {
			throw new RuntimeException("이미 투표를 완료 하셨습니다.");
		}
		
			GameVote gameVote = GameVote.builder()
					.member(member)
					.team(team)
					.game(game)
					.build();
			
			gameVoteRepository.save(gameVote);
	}
	
	public void modifyGameVote(String username,Long gameId,GameVoteRequestDTO gameVoteRequestDTO) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		Team team = teamRepository.findById(gameVoteRequestDTO.getTeamId()).orElseThrow();
		GameVote gameVote = gameVoteRepository.findByMemberIdAndGameId(member.getId(), gameId);
		
		if(gameVote == null) {
			throw new RuntimeException("투표한 데이터가 없습니다.");
		}
		
		gameVote.modifyTeam(team);
	}
	
	
	
	public void removeGameVote(Long gameId, String username) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		GameVote gameVote = gameVoteRepository.findByMemberIdAndGameId(member.getId(), gameId);
		
		if(gameVote == null) {
			throw new RuntimeException("투표한 데이터가 없습니다.");
		}
		
		gameVoteRepository.delete(gameVote);
	}
}
