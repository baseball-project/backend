package com.example.baseballprediction.domain.game.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.game.dto.GameResponse;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRatioDTO;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.constant.Status;

@ActiveProfiles("test")
@SpringBootTest
class GameServiceTest {
	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GameVoteRepository gameVoteRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private GameService gameService;

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

	@DisplayName("오늘의 승부예측 리스트를 조회한다. 로그인하지 않아도 조회가 가능하다.")
	@Test
	void findDailyGameWithNoLogin() {
		//given
		LocalDate nowDate = LocalDate.now();
		Team homeTeam1 = teamRepository.findAll().get(0);
		Team awayTeam1 = teamRepository.findAll().get(1);
		Game game1 = Game.builder()
			.status(Status.READY)
			.awayTeam(awayTeam1)
			.homeTeam(homeTeam1)
			.awayTeamScore(0)
			.homeTeamScore(0)
			.startedAt(LocalDateTime.of(nowDate, LocalTime.of(13, 0)))
			.build();

		Team homeTeam2 = teamRepository.findAll().get(2);
		Team awayTeam2 = teamRepository.findAll().get(3);

		Game game2 = Game.builder()
			.status(Status.READY)
			.awayTeam(awayTeam2)
			.homeTeam(homeTeam2)
			.awayTeamScore(0)
			.homeTeamScore(0)
			.startedAt(LocalDateTime.of(nowDate, LocalTime.of(14, 0)))
			.build();

		gameRepository.saveAll(List.of(game1, game2));

		//when
		List<GameResponse.GameDtoDaily> result = gameService.findDailyGame(null);

		//then
		GameResponse.TeamDailyDTO homeTeamDTO1 = new GameResponse.TeamDailyDTO(homeTeam1, 0, 0, homeTeam1.getId(),
			false);
		GameResponse.TeamDailyDTO awayTeamDTO1 = new GameResponse.TeamDailyDTO(awayTeam1, 0, 0, awayTeam1.getId(),
			false);
		GameResponse.TeamDailyDTO homeTeamDTO2 = new GameResponse.TeamDailyDTO(homeTeam2, 0, 0, homeTeam2.getId(),
			false);
		GameResponse.TeamDailyDTO awayTeamDTO2 = new GameResponse.TeamDailyDTO(awayTeam2, 0, 0, awayTeam2.getId(),
			false);
		assertThat(result).hasSize(2)
			.extracting("homeTeam", "awayTeam", "gameTime", "status")
			.containsExactlyInAnyOrder(
				tuple(homeTeamDTO1, awayTeamDTO1, LocalDateTime.of(nowDate, LocalTime.of(13, 0)),
					Status.READY.toString()),
				tuple(homeTeamDTO2, awayTeamDTO2, LocalDateTime.of(nowDate, LocalTime.of(14, 0)),
					Status.READY.toString())
			);
	}

	@DisplayName("오늘의 승부예측 리스트를 조회한다. 로그인을 했을 경우, 투표 정보도 같이 조회한다.")
	@Test
	void findDailyGameWithLogin() {
		//given
		LocalDate nowDate = LocalDate.now();
		Team homeTeam1 = teamRepository.findAll().get(0);
		Team awayTeam1 = teamRepository.findAll().get(1);
		Game game1 = Game.builder()
			.status(Status.READY)
			.awayTeam(awayTeam1)
			.homeTeam(homeTeam1)
			.awayTeamScore(0)
			.homeTeamScore(0)
			.startedAt(LocalDateTime.of(nowDate, LocalTime.of(13, 0)))
			.build();

		Team homeTeam2 = teamRepository.findAll().get(2);
		Team awayTeam2 = teamRepository.findAll().get(3);

		Game game2 = Game.builder()
			.status(Status.READY)
			.awayTeam(awayTeam2)
			.homeTeam(homeTeam2)
			.awayTeamScore(0)
			.homeTeamScore(0)
			.startedAt(LocalDateTime.of(nowDate, LocalTime.of(14, 0)))
			.build();

		gameRepository.saveAll(List.of(game1, game2));

		Member member = memberRepository.findAll().get(0);

		GameVote gameVote = GameVote.builder()
			.game(game1)
			.member(member)
			.team(homeTeam1)
			.build();

		gameVoteRepository.save(gameVote);

		//when
		List<GameResponse.GameDtoDaily> result = gameService.findDailyGame(member.getUsername());

		//then
		GameVoteRatioDTO gameVoteRatioDTO = gameVoteRepository.findVoteRatio(homeTeam1.getId(),
			awayTeam1.getId(), game1.getId()).orElseThrow();
		GameResponse.TeamDailyDTO homeTeamDTO1 = new GameResponse.TeamDailyDTO(homeTeam1, 0,
			gameVoteRatioDTO.getHomeTeamVoteRatio(), homeTeam1.getId(),
			true);
		GameResponse.TeamDailyDTO awayTeamDTO1 = new GameResponse.TeamDailyDTO(awayTeam1, 0,
			gameVoteRatioDTO.getAwayTeamVoteRatio(), awayTeam1.getId(),
			false);
		GameResponse.TeamDailyDTO homeTeamDTO2 = new GameResponse.TeamDailyDTO(homeTeam2, 0, 0, homeTeam2.getId(),
			false);
		GameResponse.TeamDailyDTO awayTeamDTO2 = new GameResponse.TeamDailyDTO(awayTeam2, 0, 0, awayTeam2.getId(),
			false);
		assertThat(result).hasSize(2)
			.extracting("homeTeam", "awayTeam", "gameTime", "status")
			.containsExactlyInAnyOrder(
				tuple(homeTeamDTO1, awayTeamDTO1, LocalDateTime.of(nowDate, LocalTime.of(13, 0)),
					Status.READY.toString()),
				tuple(homeTeamDTO2, awayTeamDTO2, LocalDateTime.of(nowDate, LocalTime.of(14, 0)),
					Status.READY.toString())
			);
	}

	@DisplayName("오늘의 승부예측 리스트는 오늘 날짜의 경기만 조회한다.")
	@Test
	void findDailyGameWithNow() {
		//given
		LocalDate nowDate = LocalDate.now();
		Team homeTeam1 = teamRepository.findAll().get(0);
		Team awayTeam1 = teamRepository.findAll().get(1);
		Game game1 = Game.builder()
			.status(Status.READY)
			.awayTeam(awayTeam1)
			.homeTeam(homeTeam1)
			.awayTeamScore(0)
			.homeTeamScore(0)
			.startedAt(LocalDateTime.of(nowDate, LocalTime.of(13, 0)))
			.build();

		Team homeTeam2 = teamRepository.findAll().get(2);
		Team awayTeam2 = teamRepository.findAll().get(3);

		Game game2 = Game.builder()
			.status(Status.READY)
			.awayTeam(awayTeam2)
			.homeTeam(homeTeam2)
			.awayTeamScore(0)
			.homeTeamScore(0)
			.startedAt(LocalDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.of(14, 0)))
			.build();

		gameRepository.saveAll(List.of(game1, game2));

		//when
		List<GameResponse.GameDtoDaily> result = gameService.findDailyGame(null);

		//then
		assertThat(result).hasSize(1);
	}

	@DisplayName("조회 기간을 입력하여 지난 승부예측 리스트를 조회한다.")
	@Test
	void findGameResult() {
		//given
		Team homeTeam1 = teamRepository.findAll().get(0);
		Team awayTeam1 = teamRepository.findAll().get(1);
		Game game1 = Game.builder()
			.status(Status.END)
			.homeTeam(homeTeam1)
			.awayTeam(awayTeam1)
			.homeTeamScore(3)
			.awayTeamScore(1)
			.startedAt(LocalDateTime.of(2024, 04, 01, 13, 0))
			.build();

		Team homeTeam2 = teamRepository.findAll().get(2);
		Team awayTeam2 = teamRepository.findAll().get(3);
		Game game2 = Game.builder()
			.status(Status.END)
			.homeTeam(homeTeam2)
			.awayTeam(awayTeam2)
			.homeTeamScore(5)
			.awayTeamScore(2)
			.startedAt(LocalDateTime.of(2024, 04, 02, 13, 0))
			.build();

		gameRepository.saveAll(List.of(game1, game2));

		Member member = memberRepository.findAll().get(0);

		//when
		List<GameResponse.PastGamesDTO> result = gameService.findGameResult(member.getUsername(), "2024-04-01",
			"2024-04-07");

		//then
		GameResponse.PastGameTeamDTO homeTeamDTO1 = new GameResponse.PastGameTeamDTO(homeTeam1.getId(),
			homeTeam1.getName(), 0);
		GameResponse.PastGameTeamDTO awayTeamDTO1 = new GameResponse.PastGameTeamDTO(awayTeam1.getId(),
			awayTeam1.getName(), 0);
		GameResponse.PastGameTeamDTO homeTeamDTO2 = new GameResponse.PastGameTeamDTO(homeTeam2.getId(),
			homeTeam2.getName(), 0);
		GameResponse.PastGameTeamDTO awayTeamDTO2 = new GameResponse.PastGameTeamDTO(awayTeam2.getId(),
			awayTeam2.getName(), 0);
		assertThat(result).hasSize(2)
			.extracting("gameId", "homeTeam", "awayTeam", "gameDate", "voteTeamId")
			.containsExactlyInAnyOrder(
				tuple(game1.getId(), homeTeamDTO1, awayTeamDTO1, "2024.04.01", null),
				tuple(game2.getId(), homeTeamDTO2, awayTeamDTO2, "2024.04.02", null)
			);
	}

	@DisplayName("승리팀을 업데이트한다.")
	@Test
	void updateWinTeam() {
		//given
		Team homeTeam = teamRepository.findAll().get(0);
		Team awayTeam = teamRepository.findAll().get(1);
		Game game1 = Game.builder()
			.status(Status.END)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.homeTeamScore(3)
			.awayTeamScore(1)
			.startedAt(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)))
			.build();

		//when
		game1.updateWinTeam(homeTeam);

		//then
		assertThat(game1.getWinTeam()).isEqualTo(homeTeam);
	}
}