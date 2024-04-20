package com.example.baseballprediction.domain.monthlyfairy.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.monthlyfairy.dto.MonthlyFairyResponse;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.domain.monthlyfairy.repository.MonthlyFairyRepository;
import com.example.baseballprediction.global.constant.FairyType;
import com.example.baseballprediction.global.constant.SocialType;

@ActiveProfiles("test")
@SpringBootTest
class MonthlyFairyServiceTest {

	@Autowired
	private MonthlyFairyService monthlyFairyService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MonthlyFairyRepository monthlyFairyRepository;

	@BeforeEach
	void setUp() {
		Member member1 = Member.builder()
			.username("playdot1")
			.password("123")
			.nickname("테스트유저1")
			.socialType(SocialType.KAKAO)
			.build();

		Member member2 = Member.builder()
			.username("playdot2")
			.password("123")
			.nickname("테스트유저2")
			.socialType(SocialType.KAKAO)
			.build();

		Member member3 = Member.builder()
			.username("playdot3")
			.password("123")
			.nickname("테스트유저3")
			.socialType(SocialType.KAKAO)
			.build();

		Member member4 = Member.builder()
			.username("playdot4")
			.password("123")
			.nickname("테스트유저4")
			.socialType(SocialType.KAKAO)
			.build();

		Member member5 = Member.builder()
			.username("playdot5")
			.password("123")
			.nickname("테스트유저5")
			.socialType(SocialType.KAKAO)
			.build();

		memberRepository.saveAll(List.of(member1, member2, member3, member4, member5));
	}

	@AfterEach
	void tearDown() {
		monthlyFairyRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("이번달 월간 승리요정을 조회한다.")
	@Test
	void findStatistics() {
		//given
		Member winMember1 = memberRepository.findByUsername("playdot1").orElseThrow();
		Member winMember2 = memberRepository.findByUsername("playdot2").orElseThrow();
		Member winMember3 = memberRepository.findByUsername("playdot3").orElseThrow();
		Member loseMember1 = memberRepository.findByUsername("playdot4").orElseThrow();
		Member loseMember2 = memberRepository.findByUsername("playdot5").orElseThrow();

		int month = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
		MonthlyFairy monthlyFairyWin1 = MonthlyFairy.builder()
			.type(FairyType.WIN)
			.rank(1)
			.voteRatio(70)
			.member(winMember1)
			.month(month)
			.build();

		MonthlyFairy monthlyFairyWin2 = MonthlyFairy.builder()
			.type(FairyType.WIN)
			.rank(2)
			.voteRatio(60)
			.member(winMember2)
			.month(month)
			.build();

		MonthlyFairy monthlyFairyWin3 = MonthlyFairy.builder()
			.type(FairyType.WIN)
			.rank(3)
			.voteRatio(55)
			.member(winMember3)
			.month(month)
			.build();

		MonthlyFairy monthlyFairyLose1 = MonthlyFairy.builder()
			.type(FairyType.LOSE)
			.rank(1)
			.voteRatio(10)
			.member(loseMember1)
			.month(month)
			.build();

		MonthlyFairy monthlyFairyLose2 = MonthlyFairy.builder()
			.type(FairyType.LOSE)
			.rank(2)
			.voteRatio(10)
			.member(loseMember2)
			.month(month)
			.build();

		monthlyFairyRepository.saveAll(
			List.of(monthlyFairyWin1, monthlyFairyWin2, monthlyFairyWin3, monthlyFairyLose1, monthlyFairyLose2));

		//when
		MonthlyFairyResponse.StatisticsDTO result = monthlyFairyService.findStatistics();

		//then
		assertThat(result.getWinMembers()).hasSize(3);
		assertThat(result.getWinMembers())
			.extracting("rank", "voteRatio", "nickname")
			.containsExactlyInAnyOrder(
				tuple(monthlyFairyWin1.getRank(), monthlyFairyWin1.getVoteRatio(), winMember1.getNickname()),
				tuple(monthlyFairyWin2.getRank(), monthlyFairyWin2.getVoteRatio(), winMember2.getNickname()),
				tuple(monthlyFairyWin3.getRank(), monthlyFairyWin3.getVoteRatio(), winMember3.getNickname())
			);
		assertThat(result.getLoseMembers()).hasSize(2);
		assertThat(result.getLoseMembers())
			.extracting("rank", "voteRatio", "nickname")
			.containsExactlyInAnyOrder(
				tuple(monthlyFairyLose1.getRank(), monthlyFairyLose1.getVoteRatio(), loseMember1.getNickname()),
				tuple(monthlyFairyLose2.getRank(), monthlyFairyLose2.getVoteRatio(), loseMember2.getNickname())
			);
	}
}