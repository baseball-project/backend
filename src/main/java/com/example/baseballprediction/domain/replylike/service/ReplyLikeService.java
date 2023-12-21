package com.example.baseballprediction.domain.replylike.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.domain.replylike.entity.ReplyLike;
import com.example.baseballprediction.domain.replylike.repository.ReplyLikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyLikeService {
	private final ReplyLikeRepository replyLikeRepository;
	private final ReplyRepository replyRepository;
	private final MemberRepository memberRepository;

	public void saveReplyLike(String username, Long replyId) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		Reply reply = replyRepository.findById(replyId).orElseThrow();

		ReplyLike replyLike = ReplyLike.builder()
			.member(member)
			.reply(reply)
			.build();

		replyLikeRepository.save(replyLike);
	}

	@Transactional(readOnly = true)
	public Long findReplyLikeCount(Long replyId) {
		Reply reply = replyRepository.findById(replyId).orElseThrow();

		return replyLikeRepository.countByReply(reply);
	}

	public void deleteReplyLike(String username, Long replyId) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		Reply reply = replyRepository.findById(replyId).orElseThrow();
		ReplyLike replyLike = replyLikeRepository.findByReply(reply).orElseThrow();

		if (!replyLike.getMember().equals(member)) {
			throw new RuntimeException("본인이 누른 좋아요만 취소할 수 있습니다.");
		}

		replyLikeRepository.delete(replyLike);
	}
}
