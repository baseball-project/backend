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

import com.example.baseballprediction.domain.chat.dto.ChatRequestDTO;
import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.gifttoken.repository.GiftTokenRepository;
import com.example.baseballprediction.domain.member.dto.FairyProjection;
import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.dto.ProfileProjection;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.domain.monthlyfairy.repository.MonthlyFairyRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.FairyType;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.error.exception.BusinessException;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	MonthlyFairyRepository monthlyFairyRepository;

	@Autowired
	GiftTokenRepository giftTokenRepository;

	@Autowired
	MemberServiceHelper memberServiceHelper;

	// @BeforeEach
	// void setUp() {
	// 	Team team1 = Team.builder()
	// 		.name("LG 트윈스")
	// 		.shortName("LG")
	// 		.build();
	//
	// 	Team team2 = Team.builder()
	// 		.name("한화 이글스")
	// 		.shortName("한화")
	// 		.build();
	//
	// 	teamRepository.saveAll(List.of(team1, team2));
	//
	// 	Member member1 = Member.builder()
	// 		.socialType(SocialType.KAKAO)
	// 		.nickname("테스트유저1")
	// 		.username("playdot1")
	// 		.password("123")
	// 		.build();
	//
	// 	Member member2 = Member.builder()
	// 		.socialType(SocialType.KAKAO)
	// 		.nickname("테스트유저2")
	// 		.username("playdot2")
	// 		.password("123")
	// 		.build();
	//
	// 	memberRepository.saveAll(List.of(member1, member2));
	// }

	@AfterEach
	void tearDown() {
		giftTokenRepository.deleteAllInBatch();
		monthlyFairyRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
	}

	@DisplayName("응원하는 팀을 수정한다.")
	@Test
	void modifyLikeTeam() {
		//given
		Member member = Member.builder()
			.username("playdot1")
			.password("123")
			.nickname("테스트유저1")
			.socialType(SocialType.KAKAO)
			.build();

		Member savedMember = memberRepository.save(member);

		Team favoriteTeam = Team.builder()
			.name("한화 이글스")
			.shortName("한화")
			.build();

		Team savedFavoriteTeam = teamRepository.save(favoriteTeam);

		//when
		memberService.modifyLikeTeam(savedMember.getUsername(), savedFavoriteTeam.getId());

		//then
		Team updateFavoriteTeam = memberServiceHelper.getFavoriteTeam(savedMember.getId());

		assertThat(updateFavoriteTeam)
			.extracting("id", "name", "shortName")
			.contains(1, "한화 이글스", "한화");
	}

	@DisplayName("닉네임, 한마디를 수정한다.")
	@Test
	void modifyDetails() {
		//given
		Member member = Member.builder()
			.username("playdot1")
			.password("123")
			.nickname("테스트유저1")
			.socialType(SocialType.KAKAO)
			.build();

		Member savedMember = memberRepository.save(member);

		MemberRequest.DetailsDTO detailsDTO = new MemberRequest.DetailsDTO("테스트유저2", "한마디");
		//when
		memberService.modifyDetails("playdot1", detailsDTO, null);

		//then
		Member updateMember = memberRepository.findByUsername("playdot1").orElseThrow();
		assertThat(updateMember).extracting("nickname", "comment")
			.contains("테스트유저2", "한마디");
	}

	@DisplayName("닉네임 변경 시, 중복인지 체크한다. 중복일 경우 true를 반환한다.")
	@Test
	void findExistNickname() {
		//given
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

		memberRepository.saveAll(List.of(member1, member2));

		String existNickname = "테스트유저2";
		String newNickname = "플레이닷";
		Member updateMember = memberRepository.findByUsername("playdot1").orElseThrow();

		//when
		MemberResponse.NicknameDTO trueResult = memberService.findExistNickname(updateMember.getId(),
			existNickname);
		MemberResponse.NicknameDTO falseResult = memberService.findExistNickname(updateMember.getId(),
			newNickname);

		//then
		assertThat(trueResult.isExist()).isTrue();
		assertThat(falseResult.isExist()).isFalse();
	}

	@DisplayName("특정 사용자의 닉네임, 프로필 이미지, 승리요정/패배요정 횟수를 조회한다. 승리요정 데이터가 없어도 조회가 된다.")
	@Test
	void findProfileWithNoMonthlyFairy() {
		//given
		Member member = Member.builder()
			.isNewMember(false)
			.socialType(SocialType.KAKAO)
			.username("playdot1")
			.password("123")
			.nickname("테스트유저1")
			.build();

		Member savedMember = memberRepository.save(member);

		//when
		ProfileProjection result = memberService.findProfile(savedMember.getNickname());

		//then
		assertThat(result)
			.extracting("nickname", "profileImageUrl", "winFairyCount", "loseFairyCount")
			.contains("테스트유저1", null, 0L, 0L);
	}

	@DisplayName("특정 사용자의 닉네임, 프로필 이미지, 승리요정/패배요정 횟수를 조회한다.")
	@Test
	void findProfileWithMonthlyFairy() {
		//given
		Member member = Member.builder()
			.isNewMember(false)
			.socialType(SocialType.KAKAO)
			.username("playdot1")
			.password("123")
			.nickname("테스트유저1")
			.build();

		Member savedMember = memberRepository.save(member);

		MonthlyFairy win1 = MonthlyFairy.builder()
			.member(savedMember)
			.rank(1)
			.voteRatio(70)
			.type(FairyType.WIN)
			.month(202403)
			.build();

		MonthlyFairy win2 = MonthlyFairy.builder()
			.member(savedMember)
			.rank(2)
			.voteRatio(60)
			.type(FairyType.WIN)
			.month(202402)
			.build();

		MonthlyFairy lose1 = MonthlyFairy.builder()
			.member(savedMember)
			.rank(1)
			.voteRatio(5)
			.type(FairyType.LOSE)
			.month(202401)
			.build();

		monthlyFairyRepository.saveAll(List.of(win1, win2, lose1));

		//when
		ProfileProjection result = memberService.findProfile(savedMember.getNickname());

		//then
		assertThat(result)
			.extracting("nickname", "profileImageUrl", "winFairyCount", "loseFairyCount")
			.contains("테스트유저1", null, 2L, 1L);
	}

	@DisplayName("프로필 상세정보를 조회한다.")
	@Test
	void findDetails() {
		//given
		Member member = Member.builder()
			.username("playdot1")
			.password("asd")
			.nickname("테스트유저1")
			.socialType(SocialType.KAKAO)
			.build();

		Member savedMember = memberRepository.save(member);

		//when
		MemberResponse.ProfileDTO result = memberService.findDetails(savedMember.getUsername());

		//then
		assertThat(result).extracting("nickname", "profileImageUrl", "level", "token", "teamName", "comment")
			.contains("테스트유저1", null, 1, 0);
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
		List<FairyProjection> result = memberService.findFairyStatistics(savedMember.getId());

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
		Page<MemberResponse.FairyHistoryDTO> result = memberService.findFairyHistories(savedMember.getId(), 0,
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
		Page<MemberResponse.GiftHistoryDTO> result = memberService.findGiftHistories(member.getId(), 0, 15);

		//then
		assertThat(result.get()).hasSize(1)
			.extracting("nickname", "token", "comment")
			.containsExactlyInAnyOrder(
				tuple("테스트유저2", 3, "bbbb")
			);
	}

	@DisplayName("닉네임으로 토큰을 선물한다.")
	@Test
	void saveGiftToken() {
		//given
		Member giveMember = Member.builder()
			.nickname("테스트유저1")
			.username("playdot1")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		giveMember.addToken(5);

		Member takeMember = Member.builder()
			.nickname("테스트유저2")
			.username("playdot2")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		memberRepository.saveAll(List.of(giveMember, takeMember));

		ChatRequestDTO.ChatGiftRequestDTO requestDTO = new ChatRequestDTO.ChatGiftRequestDTO("테스트유저2", 5, "잘쓰세요");

		//when
		memberService.saveGiftToken(giveMember.getId(), requestDTO);

		//then
		Member savedGiveMember = memberServiceHelper.getMember(giveMember.getId());
		Member savedTakeMember = memberServiceHelper.getMember(takeMember.getId());

		assertThat(savedGiveMember.getToken()).isEqualTo(0);
		assertThat(savedTakeMember.getToken()).isEqualTo(5);
	}

	@DisplayName("닉네임으로 토큰을 선물한다. 보유하고 있는 토큰이 선물하려는 토큰 갯수보다 적으면 예외가 발생한다.")
	@Test
	void saveGiftTokenWithLackToken() {
		//given
		Member giveMember = Member.builder()
			.nickname("테스트유저1")
			.username("playdot1")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		Member takeMember = Member.builder()
			.nickname("테스트유저2")
			.username("playdot2")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		memberRepository.saveAll(List.of(giveMember, takeMember));

		ChatRequestDTO.ChatGiftRequestDTO requestDTO = new ChatRequestDTO.ChatGiftRequestDTO("테스트유저2", 1, "잘쓰세요");

		//when
		//then
		assertThatThrownBy(() -> memberService.saveGiftToken(giveMember.getId(), requestDTO))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.INSUFFICIENT_TOKENS.getMessage());
	}

	@DisplayName("닉네임으로 토큰을 선물한다. 자기 자신한테 선물할 경우 예외가 발생한다.")
	@Test
	void saveGiftTokenWithSelf() {
		//given
		Member giveMember = Member.builder()
			.nickname("테스트유저1")
			.username("playdot1")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		Member savedMember = memberRepository.save(giveMember);

		savedMember.addToken(1);

		ChatRequestDTO.ChatGiftRequestDTO requestDTO = new ChatRequestDTO.ChatGiftRequestDTO("테스트유저1", 1, "잘쓰세요");

		//when
		//then
		assertThatThrownBy(() -> memberService.saveGiftToken(giveMember.getId(), requestDTO))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.GIFTING_TO_SELF_NOT_ALLOWED.getMessage());
	}

	@DisplayName("닉네임으로 토큰을 선물한다. 선물하기가 정상적으로 처리되면 히스토리가 저장된다.")
	@Test
	void saveGiftTokenAndsaveHistory() {
		//given
		Member giveMember = Member.builder()
			.nickname("테스트유저1")
			.username("playdot1")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		giveMember.addToken(1);

		Member takeMember = Member.builder()
			.nickname("테스트유저2")
			.username("playdot2")
			.password("123")
			.socialType(SocialType.KAKAO)
			.build();

		memberRepository.saveAll(List.of(giveMember, takeMember));

		ChatRequestDTO.ChatGiftRequestDTO requestDTO = new ChatRequestDTO.ChatGiftRequestDTO("테스트유저2", 1, "잘쓰세요");

		//when
		memberService.saveGiftToken(giveMember.getId(), requestDTO);

		//then
		GiftToken giftToken = giftTokenRepository.findAll().get(0);
		Member savedTakeMember = memberServiceHelper.getTakeMember(giftToken.getId());
		Member savedGiveMember = memberServiceHelper.getGiveMember(giftToken.getId());

		assertThat(giftToken)
			.extracting("tokenAmount", "comment")
			.contains(1, "잘쓰세요");

		assertThat(savedTakeMember.getId()).isEqualTo(giveMember.getId());
		assertThat(savedGiveMember.getId()).isEqualTo(takeMember.getId());
	}

}