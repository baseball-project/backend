package com.example.baseballprediction.domain.replylike.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.domain.replylike.entity.ReplyLike;
import com.example.baseballprediction.domain.replylike.repository.ReplyLikeRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyLikeService {
	private final ReplyLikeRepository replyLikeRepository;
	private final ReplyRepository replyRepository;
	private final MemberRepository memberRepository;

	public ReplyLike saveReplyLike(String username, Long replyId) {
		Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(
			ErrorCode.MEMBER_NOT_FOUND));

		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND));

		if (isExistLike(member, reply)) {
			throw new BusinessException(ErrorCode.REPLY_LIKE_DUPLICATED);
		}

		ReplyLike replyLike = ReplyLike.builder()
			.member(member)
			.reply(reply)
			.build();

		return replyLikeRepository.save(replyLike);
	}

	@Transactional(readOnly = true)
	private boolean isExistLike(Member member, Reply reply) {
		return replyLikeRepository.findByMemberAndReply(member, reply).isPresent();
	}

	@Transactional(readOnly = true)

	public Long findReplyLikeCount(Long replyId) {
		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND));

		return replyLikeRepository.countByReply(reply);
	}

	public void deleteReplyLike(String username, Long replyId) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND));
		ReplyLike replyLike = replyLikeRepository.findByMemberAndReply(member, reply)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_LIKE_NOT_FOUND));

		replyLikeRepository.delete(replyLike);
	}
}
