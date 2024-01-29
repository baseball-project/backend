package com.example.baseballprediction.global.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.entity.GameVote;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.domain.monthlyfairy.repository.MonthlyFairyRepository;
import com.example.baseballprediction.global.constant.FairyType;
import com.example.baseballprediction.global.util.CustomDateUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyFairyScheduleService {
	private final GameVoteRepository gameVoteRepository;
	private final GameRepository gameRepository;
	private final MemberRepository memberRepository;

	private final MonthlyFairyRepository monthlyFairyRepository;

	@Scheduled(cron = "0 0 2 1 * *", zone = "Asia/Seoul")
	public void execute() {
		aggregateMonthlyFairy(LocalDateTime.now());
	}

	@Transactional(readOnly = true)
	public void aggregateMonthlyFairy(LocalDateTime aggregateDateTime) {
		LocalDateTime aggregateDateStart = LocalDateTime.of(aggregateDateTime.getYear(), aggregateDateTime.getMonth(),
			1, 0, 0);

		LocalDate aggregateDate = CustomDateUtil.parseLocalDate(aggregateDateTime);
		LocalDateTime aggregateDateEnd = LocalDateTime.of(aggregateDate.getYear(), aggregateDate.getMonth(),
			aggregateDate.lengthOfMonth(), 23, 59);

		List<Game> games = findGamesByaggregateDate(aggregateDateStart, aggregateDateEnd);

		if (games.size() == 0)
			return;

		List<Member> members = memberRepository.findAll();

		if (members.size() == 0)
			return;

		members = aggregateVoteMembers(members, games);

		Map<FairyType, List<Member>> allFairies = findWinLoseFairies(members);

		addMonthlyFairies(allFairies);
	}

	@Transactional(readOnly = true)
	private List<Game> findGamesByaggregateDate(LocalDateTime aggregateDateStart, LocalDateTime aggregateDateEnd) {
		List<Game> games = gameRepository.findAllByStartedAtBetween(aggregateDateStart, aggregateDateEnd);

		return games;
	}

	@Transactional(readOnly = true)
	private List<Member> aggregateVoteMembers(List<Member> members, List<Game> games) {
		int gameCount = games.size();

		for (Member member : members) {
			int winVoteCount = 0;

			for (Game game : games) {
				GameVote gameVote = gameVoteRepository.findByMemberIdAndGameId(member.getId(), game.getId());
				if (gameVote == null) {
					member.sumVoteScore(-1);
					continue;
				}

				int winTeam = game.getWinTeam().getId();
				int voteTeam = gameVote.getTeam().getId();

				if (winTeam == voteTeam) {
					member.sumVoteScore(1);
					winVoteCount++;
				} else {
					member.sumVoteScore(-1);
				}
			}

			if (winVoteCount > 0) {
				double var = (double)winVoteCount / (double)gameCount;
				member.setVoteRatio((int)Math.round(var * 100));
			}
		}

		members = members.stream().sorted(Comparator.comparing(Member::getVoteScore)).toList();

		return members;
	}

	private Map<FairyType, List<Member>> findWinLoseFairies(List<Member> members) {
		List<Member> winFairies = new ArrayList<>();
		List<Member> loseFairies = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			loseFairies.add(members.get(i));
			winFairies.add(members.get(members.size() - i - 1));
		}

		Map<FairyType, List<Member>> response = new HashMap<>();

		response.put(FairyType.WIN, winFairies);
		response.put(FairyType.LOSE, loseFairies);

		return response;
	}

	private void addMonthlyFairies(Map<FairyType, List<Member>> fairies) {
		Iterator<FairyType> keys = fairies.keySet().iterator();
		while (keys.hasNext()) {
			FairyType key = keys.next();
			List<Member> members = fairies.get(key);

			for (int i = 0; i < members.size(); i++) {
				addMonthlyFairyByType(LocalDateTime.now(), key, i + 1, members.get(i));
			}
		}
	}

	@Transactional
	private void addMonthlyFairyByType(LocalDateTime aggregateDate, FairyType fairyType, int rank, Member member) {
		int month = Integer.parseInt(CustomDateUtil.dateToYearMonth(aggregateDate));

		MonthlyFairy monthlyFairy = MonthlyFairy.builder()
			.month(month)
			.member(member)
			.rank(rank)
			.voteRatio(member.getVoteRatio())
			.type(fairyType)
			.build();

		monthlyFairyRepository.save(monthlyFairy);
	}
}
