package com.example.baseballprediction.domain.member.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.gifttoken.repository.GiftTokenRepository;
import com.example.baseballprediction.domain.member.dto.FairyProjection;
import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.domain.monthlyfairy.repository.MonthlyFairyRepository;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.FairyType;
import com.example.baseballprediction.global.constant.SocialType;

@ActiveProfiles("test")
@SpringBootTest
public class HistoryServiceTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private MonthlyFairyRepository monthlyFairyRepository;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private MemberServiceHelper memberServiceHelper;

	@Autowired
	private GiftTokenRepository giftTokenRepository;

	@AfterEach
	void tearDown() {
		giftTokenRepository.deleteAllInBatch();
		monthlyFairyRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
	}

	@DisplayName("승리요정/패배요정 정보를 조회한다.")
	@Test
	void findFairyStatistics() {
		//given
		Member member = Member.builder()
			.username("playdot1")
			.password("asd")
			.nickname("테스트유저1")
			.socialType(SocialType.KAKAO)
			.build();

		Member savedMember = memberRepository.save(member);

		MonthlyFairy win1 = MonthlyFairy.builder()
			.type(FairyType.WIN)
			.month(202403)
			.member(savedMember)
			.rank(1)
			.voteRatio(70)
			.build();

		MonthlyFairy win2 = MonthlyFairy.builder()
			.type(FairyType.WIN)
			.month(202402)
			.member(savedMember)
			.rank(2)
			.voteRatio(60)
			.build();

		MonthlyFairy lose1 = MonthlyFairy.builder()
			.type(FairyType.LOSE)
			.month(202402)
			.member(savedMember)
			.rank(1)
			.voteRatio(5)
			.build();

		monthlyFairyRepository.saveAll(List.of(win1, win2, lose1));

		//when
		List<FairyProjection> result = historyService.findFairyStatistics(savedMember.getId());

		//then
		assertThat(result).hasSize(3)
			.extracting("title", "count")
			.containsExactlyInAnyOrder(
				tuple("승리요정 1등", 1),
				tuple("승리요정 2등", 1),
				tuple("패배요정 1등", 1)
			);
	}

	@DisplayName("월간 승리요정 히스토리를 조회한다.")
	@Test
	void findFairyHistories() {
		//given
		Member member = Member.builder()
			.nickname("테스트유저1")
			.username("playdot1")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		Member savedMember = memberRepository.save(member);

		MonthlyFairy win1 = MonthlyFairy.builder()
			.type(FairyType.WIN)
			.month(202403)
			.member(savedMember)
			.rank(1)
			.voteRatio(70)
			.build();

		MonthlyFairy win2 = MonthlyFairy.builder()
			.type(FairyType.WIN)
			.month(202402)
			.member(savedMember)
			.rank(2)
			.voteRatio(60)
			.build();

		MonthlyFairy lose1 = MonthlyFairy.builder()
			.type(FairyType.LOSE)
			.month(202402)
			.member(savedMember)
			.rank(1)
			.voteRatio(5)
			.build();

		monthlyFairyRepository.saveAll(List.of(win1, win2, lose1));

		//when
		Page<MemberResponse.FairyHistoryDTO> result = historyService.findFairyHistories(savedMember.getId(), 0,
			15);

		//then
		assertThat(result.get()).hasSize(3)
			.extracting("type", "rank", "comment")
			.containsExactlyInAnyOrder(
				tuple(FairyType.WIN.getName(), 1, null),
				tuple(FairyType.WIN.getName(), 2, null),
				tuple(FairyType.LOSE.getName(), 1, null)
			);
	}

	@DisplayName("특정 사용자의 보낸 선물 내역을 조회한다.")
	@Test
	void findGiftHistories() {
		//given
		Member member = Member.builder()
			.nickname("테스트유저1")
			.username("playdot1")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		Member member2 = Member.builder()
			.nickname("테스트유저2")
			.username("playdot2")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		memberRepository.saveAll(List.of(member, member2));

		GiftToken giftToken1 = GiftToken.builder()
			.tokenAmount(1)
			.giveMember(member2)
			.takeMember(member)
			.comment("aaaa")
			.build();

		GiftToken giftToken2 = GiftToken.builder()
			.tokenAmount(3)
			.giveMember(member)
			.takeMember(member2)
			.comment("bbbb")
			.build();

		giftTokenRepository.saveAll(List.of(giftToken1, giftToken2));

		//when
		Page<MemberResponse.GiftHistoryDTO> result = historyService.findGiftHistories(member.getId(), 0, 15);

		//then
		assertThat(result.get()).hasSize(1)
			.extracting("nickname", "token", "comment")
			.containsExactlyInAnyOrder(
				tuple("테스트유저2", 3, "bbbb")
			);
	}
}
