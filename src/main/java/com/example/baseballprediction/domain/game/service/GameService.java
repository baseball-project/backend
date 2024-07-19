package com.example.baseballprediction.domain.game.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.game.dto.GameResponse;
import com.example.baseballprediction.domain.game.dto.GameResponse.GameDtoDaily;
import com.example.baseballprediction.domain.game.dto.GameVoteProjection;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRatioDTO;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepositoryCustomImpl;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.util.CustomDateUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

	private final GameRepository gameRepository;
	private final GameVoteRepository gameVoteRepository;
	private final MemberRepository memberRepository;
	private final GameVoteRepositoryCustomImpl gameVoteRepositoryCustomImpl;

	public List<GameDtoDaily> findDailyGame(String username) {
		List<Game> games = gameRepository.findAll();

		List<GameDtoDaily> gameDTOList = new ArrayList<>();

		Member member = null;
		Long memberId = 0L;

		if (username != null) {
			memberId = getMemberId(username);
		}
		String currentDate = getCurrentDate();

		for (Game game : games) {
			if (isGameToday(game, currentDate)) {
				GameDtoDaily gameDto = findGameDtoDaily(game, memberId);
				gameDTOList.add(gameDto);
			}
		}

		return gameDTOList;
	}

	private Long getMemberId(String username) {
		return memberRepository.findByUsername(username)
			.map(Member::getId)
			.orElse(0L);
	}

	private String getCurrentDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	private boolean isGameToday(Game game, String currentDate) {
		return game.getStartedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd")).equals(currentDate);
	}

	private GameDtoDaily findGameDtoDaily(Game game, Long memberId) {
		GameVoteRatioDTO gameVoteRatioDTO = gameVoteRepository.findVoteRatio(game.getHomeTeam().getId(),
			game.getAwayTeam().getId(), game.getId()).orElseThrow();

		boolean homeTeamHasVoted = false;
		boolean awayTeamHasVoted = false;

		if (memberId > 0) {
			homeTeamHasVoted = gameVoteRepositoryCustomImpl.existsByGameIdAndTeamIdAndMemberId(game.getId(),
				game.getHomeTeam().getId(), memberId);
			awayTeamHasVoted = gameVoteRepositoryCustomImpl.existsByGameIdAndTeamIdAndMemberId(game.getId(),
				game.getAwayTeam().getId(), memberId);
		}

		return new GameDtoDaily(game, game.getHomeTeam(), game.getAwayTeam(), gameVoteRatioDTO, homeTeamHasVoted,
			awayTeamHasVoted);
	}

	public List<GameResponse.PastGamesDTO> findGameResult(String username, String startDate, String endDate) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		LocalDateTime startDateTime = LocalDateTime.of(CustomDateUtil.stringToDate(startDate), LocalTime.of(0, 0));
		LocalDateTime endDateTime = LocalDateTime.of(CustomDateUtil.stringToDate(endDate), LocalTime.of(23, 59));

		List<GameVoteProjection> gameVoteProjections = gameRepository.findPastGameByStartedAtBetween(member.getId(),
			startDateTime, endDateTime);

		List<GameResponse.PastGameDTO> gameResults = new ArrayList<>();

		for (GameVoteProjection gameVoteProjection : gameVoteProjections) {
			Integer homeTeamId = gameVoteProjection.getHomeTeamId();
			Integer awayTeamId = gameVoteProjection.getAwayTeamId();
			GameVoteRatioDTO gameVoteRatioDTO = gameVoteRepository.findVoteRatio(gameVoteProjection.getHomeTeamId(),
				gameVoteProjection.getAwayTeamId(), gameVoteProjection.getGameId()).orElseThrow();

			gameResults.add(new GameResponse.PastGameDTO(gameVoteProjection, gameVoteRatioDTO));
		}

		Map<String, List<GameResponse.PastGameDTO>> groupedByDate = gameResults.stream()
			.collect(Collectors.groupingBy(GameResponse.PastGameDTO::getGameDate));

		List<GameResponse.PastGamesDTO> result = groupedByDate.entrySet().stream()
			.sorted(Map.Entry.comparingByKey()) // 날짜순으로 정렬
			.map(entry -> new GameResponse.PastGamesDTO(entry.getValue()))
			.collect(Collectors.toList());

		return result;
	}

	@Transactional
	public void updateWinTeam(Game game, Team team) {
		game.updateWinTeam(team);
	}

	public GameDtoDaily findGameIdSingleCheck(Long gameId, String username) {
		Game game = gameRepository.findById(gameId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

		Member member = null;
		Long memberId = 0L;

		if (username != null) {
			memberId = getMemberId(username);
		}

		return findGameDtoDaily(game, memberId);
	}

}