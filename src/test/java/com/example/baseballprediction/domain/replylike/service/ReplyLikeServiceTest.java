package com.example.baseballprediction.domain.replylike.service;

import static org.assertj.core.api.Assertions.*;

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
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.domain.replylike.entity.ReplyLike;
import com.example.baseballprediction.domain.replylike.repository.ReplyLikeRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;

@ActiveProfiles("test")
@SpringBootTest
class ReplyLikeServiceTest {

	@Autowired
	private ReplyLikeService replyLikeService;

	@Autowired
	private ReplyLikeRepository replyLikeRepository;
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReplyRepository replyRepository;

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
		replyLikeRepository.deleteAllInBatch();
		replyRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("댓글에 좋아요를 누른다.")
	@Test
	void saveReplyLike() {
		//given
		String username = "playdot1";
		Member member = memberRepository.findByUsername(username).orElseThrow();

		Reply reply = Reply.builder()
			.parentReply(null)
			.type(ReplyType.FAIRY)
			.content("asdasd")
			.member(member)
			.build();

		Reply savedReply = replyRepository.save(reply);

		//when
		ReplyLike result = replyLikeService.saveReplyLike(username, savedReply.getId());

		//then
		assertThat(result)
			.extracting("member", "reply")
			.contains(member, savedReply);
	}

	@DisplayName("이미 좋아요를 누른 댓글일 경우, 한번 더 좋아요를 누르면 예외가 발생한다.")
	@Test
	void saveReplyLikeWithExist() {
		//given
		String username = "playdot1";
		Member member = memberRepository.findByUsername(username).orElseThrow();

		Reply reply = Reply.builder()
			.parentReply(null)
			.type(ReplyType.FAIRY)
			.content("asdasd")
			.member(member)
			.build();

		Reply savedReply = replyRepository.save(reply);
		ReplyLike replyLike = ReplyLike.builder()
			.reply(savedReply)
			.member(member)
			.build();

		replyLikeRepository.save(replyLike);

		//when
		//then
		assertThatThrownBy(() -> replyLikeService.saveReplyLike(username, savedReply.getId()))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.REPLY_LIKE_DUPLICATED.getMessage());

	}

	@DisplayName("댓글의 좋아요 갯수를 조회한다.")
	@Test
	void findReplyLikeCount() {
		//given
		String username = "playdot1";
		Member member1 = memberRepository.findByUsername(username).orElseThrow();
		Member member2 = memberRepository.findByUsername("playdot2").orElseThrow();
		Member member3 = memberRepository.findByUsername("playdot3").orElseThrow();

		Reply reply = Reply.builder()
			.parentReply(null)
			.type(ReplyType.FAIRY)
			.content("asdasd")
			.member(member1)
			.build();

		Reply savedReply = replyRepository.save(reply);
		ReplyLike replyLike1 = ReplyLike.builder()
			.reply(savedReply)
			.member(member1)
			.build();

		ReplyLike replyLike2 = ReplyLike.builder()
			.reply(savedReply)
			.member(member2)
			.build();

		ReplyLike replyLike3 = ReplyLike.builder()
			.reply(savedReply)
			.member(member3)
			.build();

		replyLikeRepository.saveAll(List.of(replyLike1, replyLike2, replyLike3));

		//when
		Long result = replyLikeService.findReplyLikeCount(savedReply.getId());

		//then
		assertThat(result).isEqualTo(3L);
	}

	@DisplayName("댓글 좋아요를 취소한다.")
	@Test
	void deleteReplyLike() {
		//given
		String username = "playdot1";
		Member member = memberRepository.findByUsername(username).orElseThrow();

		Reply reply = Reply.builder()
			.parentReply(null)
			.type(ReplyType.FAIRY)
			.content("asdasd")
			.member(member)
			.build();

		Reply savedReply = replyRepository.save(reply);
		ReplyLike replyLike = ReplyLike.builder()
			.reply(savedReply)
			.member(member)
			.build();

		replyLikeRepository.save(replyLike);

		//when
		replyLikeService.deleteReplyLike(username, savedReply.getId());

		//then
		assertThatThrownBy(() -> replyLikeRepository.findByReply(savedReply).orElseThrow(() -> new NotFoundException(
			ErrorCode.REPLY_LIKE_NOT_FOUND)))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.REPLY_LIKE_NOT_FOUND.getMessage());
	}

	@DisplayName("사용자가 누른 좋아요가 없을 경우, 좋아요 취소 시 예외가 발생한다.")
	@Test
	void deleteReplyLikeWithNoMember() {
		//given
		String username = "playdot1";
		Member member1 = memberRepository.findByUsername(username).orElseThrow();
		Member member2 = memberRepository.findByUsername("playdot2").orElseThrow();

		Reply reply = Reply.builder()
			.parentReply(null)
			.type(ReplyType.FAIRY)
			.content("asdasd")
			.member(member1)
			.build();

		Reply savedReply = replyRepository.save(reply);
		ReplyLike replyLike1 = ReplyLike.builder()
			.reply(savedReply)
			.member(member1)
			.build();
		ReplyLike replyLike2 = ReplyLike.builder()
			.reply(savedReply)
			.member(member2)
			.build();

		replyLikeRepository.saveAll(List.of(replyLike1, replyLike2));

		//when
		//then
		assertThatThrownBy(() -> replyLikeService.deleteReplyLike("playdot3", savedReply.getId()))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.REPLY_LIKE_NOT_FOUND.getMessage());
	}
}