package com.example.baseballprediction.domain.chat.minigame.service;

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

import com.example.baseballprediction.domain.chat.dto.ChatEventDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO;
import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;
import com.example.baseballprediction.domain.chat.minigame.repository.MiniGameRepository;
import com.example.baseballprediction.domain.chat.minigamevote.entity.MiniGameVote;
import com.example.baseballprediction.domain.chat.minigamevote.repository.MiniGameVoteRepository;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
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
class MiniGameServiceTest {

	@Autowired
	private MiniGameRepository miniGameRepository;

	@Autowired
	private MiniGameVoteRepository miniGameVoteRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private MiniGameService miniGameService;

	@Autowired
	private TeamRepository teamRepository;

	@BeforeEach
	void setUp() {
		Team team1 = createTeam("한화 이글스", "한화");
		Team team2 = createTeam("LG 트윈스", "LG");
		Team team3 = createTeam("NC 다이노스", "NC");
		Team team4 = createTeam("두산 베어스", "두산");

		teamRepository.saveAll(List.of(team1, team2, team3, team4));

		Member member1 = createMember("playdot1", "123", "테스트유저1");
		member1.addToken(5);
		Member member2 = createMember("playdot2", "123", "테스트유저2");
		Member member3 = createMember("playdot3", "123", "테스트유저3");
		Member member4 = createMember("playdot4", "123", "테스트유저4");
		Member member5 = createMember("playdot5", "123", "테스트유저5");

		member1.changeTeam(team1);

		memberRepository.saveAll(List.of(member1, member2, member3, member4, member5));

	}

	@AfterEach
	void tearDown() {
		miniGameVoteRepository.deleteAllInBatch();
		miniGameRepository.deleteAllInBatch();
		gameRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
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

	@DisplayName("미니게임 투표를 생성한다.")
	@Test
	void saveCreateVote() {
		//given
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot1").orElseThrow();

		//when
		MiniGame minigame = miniGameService.saveCreateVote(game.getId(), option, member.getNickname());

		//then
		assertThat(minigame)
			.extracting("game", "member", "question", "option1", "option2", "status")
			.contains(game, member, option.getQuestion(), option.getOption1(), option.getOption2(), Status.PROGRESS);

	}

	@DisplayName("미니게임 투표를 생성한다. 토큰이 부족하면 예외가 발생한다.")
	@Test
	void saveCreateVoteWithLackToken() {
		//given
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot2").orElseThrow();

		//when
		//then
		assertThatThrownBy(() -> miniGameService.saveCreateVote(game.getId(), option, member.getNickname()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.MINI_GAME_TOKENS_INSUFFICIENT.getMessage());
	}

	@DisplayName("미니게임 투표를 생성한다. 최대 투표 생성 갯수를 넘기면 예외가 발생한다.")
	@Test
	void saveCreateVoteWithMaxCount() {
		//given
		miniGameService.setVoteCountPerGame(1L, 41);
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot1").orElseThrow();

		//when
		//then
		assertThatThrownBy(() -> miniGameService.saveCreateVote(1L, option, member.getNickname()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.MINI_GAME_MAX_VOTE_LIMIT.getMessage());
	}

	@DisplayName("미니투표 투표를 한다.")
	@Test
	void addVote() {
		//given
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot1").orElseThrow();

		MiniGame miniGame = MiniGame.builder()
			.status(Status.PROGRESS)
			.game(game)
			.member(member)
			.question(option.getQuestion())
			.option1(option.getOption1())
			.option2(option.getOption2())
			.build();

		miniGameRepository.save(miniGame);

		//when
		boolean result = miniGameService.addVote(miniGame.getId(), member.getNickname(), 1);

		//then
		assertThat(result).isTrue();
	}

	@DisplayName("미니투표 투표를 한다. 미니투표 상태가 준비면 예외가 발생한다.")
	@Test
	void addVoteWithStatusReady() {
		//given
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot1").orElseThrow();

		MiniGame miniGame = MiniGame.builder()
			.status(Status.READY)
			.game(game)
			.member(member)
			.question(option.getQuestion())
			.option1(option.getOption1())
			.option2(option.getOption2())
			.build();

		miniGameRepository.save(miniGame);

		//when
		//then
		assertThatThrownBy(() -> miniGameService.addVote(miniGame.getId(), member.getNickname(), 1))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.MINI_GAME_CURRENTLY_WAITING.getMessage());
	}

	@DisplayName("미니투표 투표를 한다. 미니투표 상태가 종료면 예외가 발생한다.")
	@Test
	void addVoteWithStatusEnd() {
		//given
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot1").orElseThrow();

		MiniGame miniGame = MiniGame.builder()
			.status(Status.END)
			.game(game)
			.member(member)
			.question(option.getQuestion())
			.option1(option.getOption1())
			.option2(option.getOption2())
			.build();

		miniGameRepository.save(miniGame);

		//when
		//then
		assertThatThrownBy(() -> miniGameService.addVote(1L, member.getNickname(), 1))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.MINI_GAME_ALREADY_ENDED.getMessage());
	}

	@DisplayName("미니투표 결과를 조회한다.")
	@Test
	void findPerformVoteAndGetResults() {
		//given
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot1").orElseThrow();

		MiniGame miniGame = MiniGame.builder()
			.status(Status.PROGRESS)
			.game(game)
			.member(member)
			.question(option.getQuestion())
			.option1(option.getOption1())
			.option2(option.getOption2())
			.build();

		miniGameRepository.save(miniGame);

		MiniGameVote vote = MiniGameVote.builder()
			.voteOption(1)
			.member(member)
			.miniGame(miniGame)
			.build();

		miniGameVoteRepository.save(vote);

		//when
		MiniGameVoteDTO.VoteResultDTO result = miniGameService.findPerformVoteAndGetResults(miniGame.getId(),
			member.getNickname());

		//then
		MiniGameVoteDTO.VoteCreator creator = new MiniGameVoteDTO.VoteCreator(member.getNickname(), option);
		ChatEventDTO.ChatProfileDTO profile = new ChatEventDTO.ChatProfileDTO(member.getNickname(),
			member.getProfileImageUrl(), (String)null);
		MiniGameVoteDTO.VoteRatio ratio = new MiniGameVoteDTO.VoteRatio(100, 0);

		assertThat(result.getVoteCreator().getCreatorNickname()).isEqualTo(member.getNickname());
		assertThat(result.getMyProfile()).extracting("nickname", "profileImageUrl", "teamName")
			.contains(member.getNickname(), member.getProfileImageUrl(), null);
		assertThat(result.getVoteRatio()).extracting("option1VoteRatio", "option2VoteRatio")
			.contains(100, 0);

	}

	@DisplayName("생성한 미니투표를 취소하고 토큰을 환불한다.")
	@Test
	void saveCancelledVotesAndRefundTokens() {
		//given
		MiniGameVoteDTO.Options option = new MiniGameVoteDTO.Options("질문1", "선택1", "선택2");
		Team homeTeam = teamRepository.findByShortName("한화").orElseThrow();
		Team awayTeam = teamRepository.findByShortName("NC").orElseThrow();

		LocalDateTime startedAt = LocalDateTime.now();
		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.build();

		gameRepository.save(game);

		Member member = memberRepository.findByUsername("playdot1").orElseThrow();

		MiniGame miniGame = MiniGame.builder()
			.status(Status.PROGRESS)
			.game(game)
			.member(member)
			.question(option.getQuestion())
			.option1(option.getOption1())
			.option2(option.getOption2())
			.build();

		miniGameRepository.save(miniGame);

		//when
		miniGameService.saveCancelledVotesAndRefundTokens(game.getId());

		//then
		List<MiniGame> result = miniGameRepository.findAll();

		assertThat(result).extracting("status")
			.containsExactlyInAnyOrder(Status.CANCEL);

		Member resultMember = memberRepository.findByUsername("playdot1").orElseThrow();

		assertThat(resultMember.getToken()).isEqualTo(10);

	}
}