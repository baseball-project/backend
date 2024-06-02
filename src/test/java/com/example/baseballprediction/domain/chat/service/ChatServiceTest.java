package com.example.baseballprediction.domain.chat.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.chat.dto.GameTeamType;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.constant.Status;
import com.example.baseballprediction.global.error.exception.NotFoundException;

@ActiveProfiles("test")
@SpringBootTest
class ChatServiceTest {
	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private GameVoteRepository gameVoteRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ChatService chatService;

	@BeforeEach
	void setUp() {
		Team homeTeam = Team.builder()
			.name("한화 이글스")
			.shortName("한화")
			.build();
		Team awayTeam = Team.builder()
			.name("두산 베어스")
			.shortName("두산")
			.build();

		teamRepository.saveAll(List.of(homeTeam, awayTeam)
		);

		Game game = Game.builder()
			.status(Status.PROGRESS)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0)))
			.build();

		gameRepository.save(game);

		Member member = Member.builder()
			.username("playdot1")
			.password("password")
			.nickname("테스트유저1")
			.socialType(SocialType.KAKAO)
			.build();

		memberRepository.save(member);
	}

	@AfterEach
	void tearDown() {
		gameVoteRepository.deleteAllInBatch();
		gameRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
	}

	@DisplayName("경기 채팅방 세션에 사용자 세션을 추가한다. 채팅방이 존재하지 않으면 새로 생성한다.")
	@Test
	void addChatRoom() {
		//given
		Game game = gameRepository.findAll().get(0);
		String sessionId = "session";

		//when
		chatService.addChatRoom(sessionId, game.getId());

		//then
		Set<String> result = chatService.getUsersInChatRoom(game.getId());
		assertThat(result).hasSize(1);
	}

	@DisplayName("사용자가 채팅방을 나가면 해당 사용자의 세션을 제거한다.")
	@Test
	void removeMembeSessionChatRoom() {
		//given
		Game game = gameRepository.findAll().get(0);
		String sessionId = "session";
		chatService.addChatRoom(sessionId, game.getId());

		//when
		chatService.removeMembeSessionChatRoom(sessionId, game.getId());

		//then
		Set<String> result = chatService.getUsersInChatRoom(game.getId());
		assertThat(result).isEmpty();
	}

	@DisplayName("사용자가 채팅방을 나가면 해당 사용자의 세션을 제거한다. 채팅방을 찾지 못하면 예외가 발생한다.")
	@Test
	void removeMembeSessionChatRoomWithNotFound() {
		//given
		Game game = gameRepository.findAll().get(0);
		String sessionId = "session";
		chatService.addChatRoom(sessionId, game.getId());

		//when
		//then
		assertThatThrownBy(() -> chatService.removeMembeSessionChatRoom(sessionId, 2L))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
	}

	@DisplayName("채팅방을 닫는다.")
	@Test
	void closeChatRoom() {
		//given
		Game game = gameRepository.findAll().get(0);
		String sessionId = "session";
		chatService.addChatRoom(sessionId, game.getId());

		//when
		chatService.closeChatRoom(game.getId());

		//then
		boolean result = chatService.isExistChatRoom(game.getId());
		assertThat(result).isFalse();

	}

	@DisplayName("사용자가 투표한 팀이 해당 경기에서 홈팀인지 어웨이팀인지 조회한다.")
	@Test
	void findDailyGameTeamType() {
		//given
		Game game = gameRepository.findAll().get(0);
		Team homeTeam = game.getHomeTeam();
		Member member = memberRepository.findAll().get(0);
		GameVote gameVote = GameVote.builder()
			.team(homeTeam)
			.member(member)
			.game(game)
			.build();

		gameVoteRepository.save(gameVote);

		//when
		GameTeamType result = chatService.findDailyGameTeamType(game.getId(), member.getId());

		//then
		assertThat(result.getTeamType()).isEqualTo("home");
	}
}