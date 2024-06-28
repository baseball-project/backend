package com.example.baseballprediction.domain.reply.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.reply.dto.ReplyReportResponse;
import com.example.baseballprediction.domain.reply.dto.ReplyResponse;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.entity.ReplyReport;
import com.example.baseballprediction.domain.reply.repository.ReplyReportRepository;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.domain.replylike.entity.ReplyLike;
import com.example.baseballprediction.domain.replylike.repository.ReplyLikeRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.ReplyStatus;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.constant.ReportType;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.error.exception.ReplyMemberInvalidException;

@ActiveProfiles("test")
@SpringBootTest
class ReplyServiceTest {

	@Autowired
	private ReplyService replyService;

	@Autowired
	private ReplyRepository replyRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReplyReportRepository replyReportRepository;

	@Autowired
	private ReplyLikeRepository replyLikeRepository;

	private Member findMember(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

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

		memberRepository.saveAll(List.of(member1, member2, member3, member4));
	}

	@AfterEach
	void tearDown() {
		replyReportRepository.deleteAllInBatch();
		replyLikeRepository.deleteAllInBatch();
		replyRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("댓글 타입이 FAIRY이고, 입력받은 페이지번호와 조회건수, 사용자 ID를 통해 월간 승리요정 댓글 리스트를 조회한다.")
	@Test
	void findRepliesByTypeInFairy() {
		//given
		int page = 0;
		int size = 15;
		String username = "playdot1";

		Member member1 = findMember("playdot1");
		Member member2 = findMember("playdot2");

		Reply reply1 = createReply(member1, null, "AAA", ReplyType.FAIRY);
		Reply reply2 = createReply(member2, null, "BBB", ReplyType.FAIRY);
		Reply reply3 = createReply(member1, null, "CCC", ReplyType.GAME);

		replyRepository.saveAll(List.of(reply1, reply2, reply3));

		//when
		Page<ReplyResponse.ReplyDTO> result = replyService.findRepliesByType(ReplyType.FAIRY, page, size, username);

		//then

		assertThat(result).hasSize(2)
			.extracting("content", "nickname")
			.containsExactlyInAnyOrder(
				Tuple.tuple("AAA", "테스트유저1"),
				Tuple.tuple("BBB", "테스트유저2")
			);
	}

	@DisplayName("댓글 타입이 GAME이고, 입력받은 페이지번호와 조회건수, 사용자 ID를 통해 오늘의 승부예측 댓글 리스트를 조회한다.")
	@Test
	void findRepliesByGame() {
		//given
		int page = 0;
		int size = 15;
		String username = "playdot1";

		Member member1 = findMember("playdot1");
		Member member2 = findMember("playdot2");

		Reply reply1 = createReply(member1, null, "AAA", ReplyType.FAIRY);

		Reply reply2 = createReply(member2, null, "BBB", ReplyType.FAIRY);
		Reply reply3 = createReply(member1, null, "CCC", ReplyType.GAME);

		replyRepository.saveAll(List.of(reply1, reply2, reply3));

		//when
		Page<ReplyResponse.ReplyDTO> result = replyService.findRepliesByType(ReplyType.GAME, page, size, username);

		//then

		assertThat(result).hasSize(1)
			.extracting("content", "nickname")
			.containsExactlyInAnyOrder(
				Tuple.tuple("CCC", "테스트유저1")
			);
	}

	@DisplayName("입력한 댓글 타입, 사용자ID, 내용을 통해 댓글을 작성한다.")
	@Test
	void addReply() {
		//given
		ReplyType replyType = ReplyType.FAIRY;
		String username = "playdot1";
		String content = "asdasd";

		//when
		Reply savedReply = replyService.addReply(replyType, username, content);

		//then
		assertThat(savedReply)
			.extracting("content", "type")
			.contains("asdasd", ReplyType.FAIRY);
	}

	@DisplayName("월간 승리요정 댓글에 대댓글을 작성한다.")
	@Test
	void addSubReply() {
		//given
		ReplyType replyType = ReplyType.FAIRY;
		String username = "playdot1";
		String content = "asdasd";

		Member member = memberRepository.findByUsername(username).orElseThrow();
		Reply parentReply = createReply(member, null, content, replyType);

		Reply savedParentReply = replyRepository.save(parentReply);

		//when
		Reply subReply = replyService.addSubReply(savedParentReply.getId(), ReplyType.FAIRY, username, "asdaaa");

		//then
		assertThat(subReply.getParentReply().getId()).isEqualTo(parentReply.getId());
	}

	@DisplayName("본인이 작성한 댓글을 삭제한다.")
	@Test
	void deleteReply() {
		//given
		ReplyType replyType = ReplyType.FAIRY;
		String username = "playdot1";
		String content = "asdasd";

		Member member = memberRepository.findByUsername(username).orElseThrow();
		Reply reply = createReply(member, null, content, replyType);

		Reply savedReply = replyRepository.save(reply);

		//when
		replyService.deleteReply(savedReply.getId(), username);

		//then
		assertThatThrownBy(
			() -> replyRepository.findById(savedReply.getId())
				.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND)))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.REPLY_NOT_FOUND.getMessage());
	}

	@DisplayName("작성자가 아닌 다른 사용자가 댓글을 삭제하려는 경우 예외가 발생한다.")
	@Test
	void deleteReplyWithAnotherMember() {
		//given
		ReplyType replyType = ReplyType.FAIRY;
		String editorUsername = "playdot1";
		String content = "asdasd";

		Member editor = memberRepository.findByUsername(editorUsername).orElseThrow();

		Reply reply = createReply(editor, null, content, replyType);

		Reply savedReply = replyRepository.save(reply);

		String username = "playdot2";

		//when
		//then
		assertThatThrownBy(() -> replyService.deleteReply(savedReply.getId(), username))
			.isInstanceOf(ReplyMemberInvalidException.class)
			.hasMessage(ErrorCode.REPLY_MEMBER_INVALID.getMessage());
	}

	@DisplayName("부모 댓글에 작성한 대댓글을 조회한다.")
	@Test
	void findSubReplies() {
		//given
		ReplyType replyType = ReplyType.FAIRY;
		String username = "playdot1";
		String parentContent = "aaaaaaa";

		Member member = memberRepository.findByUsername(username).orElseThrow();
		Reply parentReply = Reply.builder()
			.type(replyType)
			.member(member)
			.parentReply(null)
			.content(parentContent)
			.build();

		Reply savedParentReply = replyRepository.save(parentReply);

		Reply subReply1 = createReply(member, savedParentReply, "aaaaaaaa", replyType);

		Reply subReply2 = createReply(member, savedParentReply, "bbbbbbbb", replyType);

		Reply subReply3 = createReply(member, savedParentReply, "cccccccc", replyType);

		replyRepository.saveAll(List.of(subReply1, subReply2, subReply3));

		//when
		List<ReplyResponse.ReplyDTO> result = replyService.findSubReplies(savedParentReply.getId(), username);

		//then
		assertThat(result).hasSize(3)
			.extracting("nickname", "content")
			.containsExactlyInAnyOrder(
				tuple(member.getNickname(), "aaaaaaaa"),
				tuple(member.getNickname(), "bbbbbbbb"),
				tuple(member.getNickname(), "cccccccc")
			);
	}

	@DisplayName("댓글 신고 항목을 조회한다.")
	@Test
	void findReportTypes() {
		//given

		//when
		List<ReplyReportResponse.ListDTO> result = replyService.findReportTypes();

		//then
		assertThat(result).hasSize(4)
			.extracting("type", "comment")
			.containsExactlyInAnyOrder(
				tuple("ABUSE", "부적절한 언어 사용"),
				tuple("SPAM", "스팸/홍 및 도배글"),
				tuple("PRIVACY", "개인정보 노출"),
				tuple("ETC", "기타")
			);
	}

	@DisplayName("댓글 신고를 등록한다.")
	@Test
	void addReplyReport() {
		//given
		Member editor = memberRepository.findByUsername("playdot1").orElseThrow();

		String reporterUsername = "playdot2";
		Member reporter = memberRepository.findByUsername(reporterUsername).orElseThrow();
		String content = "asdasdasdasd";

		Reply reply = createReply(editor, null, content, ReplyType.FAIRY);

		Reply savedReply = replyRepository.save(reply);

		//when
		ReplyReport result = replyService.addReplyReport(savedReply.getId(), reporterUsername, ReportType.ETC);

		//then
		assertThat(result)
			.extracting("reply", "member", "reportType")
			.contains(savedReply, reporter, ReportType.ETC);

	}

	@DisplayName("댓글이 신고가 3개 이상 누적될 경우, 댓글의 상태는 BLIND로 업데이트된다.")
	@Test
	void updateReplyStatusBlindByReportCount() {
		//given
		Member editor = memberRepository.findByUsername("playdot1").orElseThrow();

		String reporterUsername1 = "playdot2";
		String reporterUsername2 = "playdot3";
		String reporterUsername3 = "playdot4";
		Member reporter1 = memberRepository.findByUsername(reporterUsername1).orElseThrow();
		Member reporter2 = memberRepository.findByUsername(reporterUsername2).orElseThrow();
		Member reporter3 = memberRepository.findByUsername(reporterUsername3).orElseThrow();
		String content = "asdasdasdasd";

		Reply reply = createReply(editor, null, content, ReplyType.FAIRY);

		Reply savedReply = replyRepository.save(reply);

		//when
		ReplyReport replyReport1 = replyService.addReplyReport(savedReply.getId(), reporterUsername1, ReportType.ETC);
		ReplyReport replyReport2 = replyService.addReplyReport(savedReply.getId(), reporterUsername2, ReportType.ETC);
		ReplyReport replyReport3 = replyService.addReplyReport(savedReply.getId(), reporterUsername3, ReportType.ETC);

		Reply result = replyRepository.findById(savedReply.getId()).orElseThrow();

		//then
		assertThat(result.getStatus().equals(ReplyStatus.BLIND));
	}

	@DisplayName("댓글 신고는 한번만 가능하다. 이미 신고한 댓글을 또 신고할 경우 예외가 발생한다.")
	@Test
	void isExistReport() {
		//given
		String reporterUsername = "playdot2";
		Member editor = memberRepository.findByUsername("playdot1").orElseThrow();
		String content = "asd";

		Reply reply = createReply(editor, null, content, ReplyType.FAIRY);
		Reply savedReply = replyRepository.save(reply);

		ReplyReport replyReport = ReplyReport.builder()
			.reportType(ReportType.ETC)
			.member(memberRepository.findByUsername(reporterUsername).orElseThrow())
			.reply(savedReply)
			.build();

		replyReportRepository.save(replyReport);

		//when
		//then
		assertThatThrownBy(() -> replyService.addReplyReport(savedReply.getId(), reporterUsername, ReportType.ETC))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.REPLY_REPORT_EXIST.getMessage());
	}

	@DisplayName("댓글을 삭제하면 좋아요도 삭제된다.")
	@Test
	void deleteReplyCascadeLike() {
		//given
		ReplyType replyType = ReplyType.FAIRY;
		String username = "playdot1";
		String content = "asdasd";

		Member member = memberRepository.findByUsername(username).orElseThrow();
		Reply reply = createReply(member, null, content, replyType);

		Reply savedReply = replyRepository.save(reply);

		ReplyLike replyLike = ReplyLike.builder()
			.member(member)
			.reply(reply)
			.build();

		replyLikeRepository.save(replyLike);

		//when
		replyService.deleteReply(savedReply.getId(), username);

		//then
		assertThatThrownBy(
			() -> replyLikeRepository.findById(replyLike.getId())
				.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_LIKE_NOT_FOUND)))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.REPLY_LIKE_NOT_FOUND.getMessage());
	}

	private Reply createReply(Member member, Reply parentReply, String content, ReplyType replyType) {
		return Reply.builder()
			.type(replyType)
			.content(content)
			.parentReply(parentReply)
			.member(member)
			.build();
	}
}