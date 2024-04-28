package com.example.baseballprediction.domain.gamevote.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRequest;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.constant.Status;
import com.example.baseballprediction.global.error.exception.BusinessException;

@ActiveProfiles("test")
@SpringBootTest
class GameVoteServiceTest {
	@Autowired
	private GameVoteService gameVoteService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GameVoteRepository gameVoteRepository;

	@BeforeEach
	void setUp() {
		Member member1 = createMember("playdot1", "123", "테스트유저1");
		Member member2 = createMember("playdot2", "123", "테스트유저2");
		Member member3 = createMember("playdot3", "123", "테스트유저3");
		Member member4 = createMember("playdot4", "123", "테스트유저4");
		Member member5 = createMember("playdot5", "123", "테스트유저5");

		memberRepository.saveAll(List.of(member1, member2, member3, member4, member5));

		Team team1 = createTeam("한화 이글스", "한화");
		Team team2 = createTeam("LG 트윈스", "LG");
		Team team3 = createTeam("NC 다이노스", "NC");
		Team team4 = createTeam("두산 베어스", "두산");

		teamRepository.saveAll(List.of(team1, team2, team3, team4));
	}

	private Team createTeam(String name, String shortName) {
		return Team.builder()
			.name(name)
			.shortName(shortName)
			.build();
	}

	private Member createMember(String username, String password, String nickname) {
		return Member.builder()
			.username(username)
			.password(password)
			.nickname(nickname)
			.socialType(SocialType.KAKAO)
			.build();
	}

	@AfterEach
	void tearDown() {
		gameVoteRepository.deleteAllInBatch();
		gameRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
	}

	@DisplayName("오늘의 승부예측 투표를 저장한다.")
	@Test
	void addGameVote() {
		//given
		Member member = memberRepository.findAll().get(0);
		Game game = Game.builder()
			.status(Status.READY)
			.awayTeam(teamRepository.findAll().get(1))
			.homeTeam(teamRepository.findAll().get(0))
			.startedAt(LocalDateTime.now().minusMinutes(20))
			.build();
		gameRepository.save(game);
		Team homeTeam = game.getHomeTeam();
		GameVoteRequest.GameVoteRequestDTO request = new GameVoteRequest.GameVoteRequestDTO(homeTeam.getId());

		//when
		GameVote result = gameVoteService.addGameVote(member.getUsername(), game.getId(), request);

		//then
		assertThat(result)
			.extracting("member", "team", "game")
			.contains(member, homeTeam, game);

	}

	@DisplayName("이미 투표한 경기에 중복해서 투표를 할 경우 예외가 발생한다.")
	@Test
	void addGameVoteWithDuplicated() {
		//given
		Member member = memberRepository.findAll().get(0);
		Game game = Game.builder()
			.status(Status.READY)
			.awayTeam(teamRepository.findAll().get(1))
			.homeTeam(teamRepository.findAll().get(0))
			.startedAt(LocalDateTime.now().minusMinutes(20))
			.build();
		gameRepository.save(game);
		Team homeTeam = game.getHomeTeam();
		GameVote gameVote = GameVote.builder()
			.team(homeTeam)
			.game(game)
			.member(member)
			.build();
		gameVoteRepository.save(gameVote);
		GameVoteRequest.GameVoteRequestDTO request = new GameVoteRequest.GameVoteRequestDTO(homeTeam.getId());

		//when
		//then
		assertThatThrownBy(() -> gameVoteService.addGameVote(member.getUsername(), game.getId(), request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.VOTING_ALREADY_COMPLETED.getMessage());

	}

	@DisplayName("투표한 팀을 수정한다.")
	@Test
	void modifyGameVote() {
		//given
		Member member = memberRepository.findAll().get(0);
		Game game = Game.builder()
			.status(Status.READY)
			.awayTeam(teamRepository.findAll().get(1))
			.homeTeam(teamRepository.findAll().get(0))
			.startedAt(LocalDateTime.now().minusMinutes(20))
			.build();
		gameRepository.save(game);
		Team homeTeam = game.getHomeTeam();
		Team awayTeam = game.getAwayTeam();
		GameVote gameVote = GameVote.builder()
			.team(homeTeam)
			.game(game)
			.member(member)
			.build();
		gameVoteRepository.save(gameVote);
		GameVoteRequest.GameVoteRequestDTO request = new GameVoteRequest.GameVoteRequestDTO(awayTeam.getId());

		//when
		gameVoteService.modifyGameVote(member.getUsername(), game.getId(), request);

		//then
		GameVote result = gameVoteRepository.findById(gameVote.getId()).orElseThrow();
		assertThat(result.getTeam().getId()).isEqualTo(awayTeam.getId());
	}

	@DisplayName("다른 사용자의 투표를 수정할 경우 예외가 발생한다.")
	@Test
	void modifyGameVoteWithOtherMember() {
		//given
		Member voteMember = memberRepository.findAll().get(0);
		Member member = memberRepository.findAll().get(1);
		Game game = Game.builder()
			.status(Status.READY)
			.awayTeam(teamRepository.findAll().get(1))
			.homeTeam(teamRepository.findAll().get(0))
			.startedAt(LocalDateTime.now().minusMinutes(20))
			.build();
		gameRepository.save(game);
		Team homeTeam = game.getHomeTeam();
		Team awayTeam = game.getAwayTeam();
		GameVote gameVote = GameVote.builder()
			.team(homeTeam)
			.game(game)
			.member(voteMember)
			.build();
		gameVoteRepository.save(gameVote);
		GameVoteRequest.GameVoteRequestDTO request = new GameVoteRequest.GameVoteRequestDTO(awayTeam.getId());

		//when
		//then
		assertThatThrownBy(() -> gameVoteService.modifyGameVote(member.getUsername(), game.getId(), request))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.VOTING_DATA_NOT_FOUND.getMessage());
	}

	@DisplayName("투표를 취소한다.")
	@Test
	void removeGameVote() {
		//given
		Member member = memberRepository.findAll().get(0);
		Game game = Game.builder()
			.status(Status.READY)
			.awayTeam(teamRepository.findAll().get(1))
			.homeTeam(teamRepository.findAll().get(0))
			.startedAt(LocalDateTime.now().minusMinutes(20))
			.build();
		gameRepository.save(game);
		Team homeTeam = game.getHomeTeam();
		Team awayTeam = game.getAwayTeam();
		GameVote gameVote = GameVote.builder()
			.team(homeTeam)
			.game(game)
			.member(member)
			.build();
		gameVoteRepository.save(gameVote);

		//when
		gameVoteService.removeGameVote(game.getId(), member.getUsername());

		//then
		List<GameVote> result = gameVoteRepository.findAll();
		assertThat(result).isEmpty();
	}
}