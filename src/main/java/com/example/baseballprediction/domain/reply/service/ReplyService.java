package com.example.baseballprediction.domain.reply.service;

import static com.example.baseballprediction.domain.reply.dto.ReplyResponse.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.baseballprediction.domain.game.dto.GameReplyDTO;
import com.example.baseballprediction.domain.game.dto.GameReplyLikeProjection.GameListDTO;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.error.exception.ReplyMemberInvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {
	private final ReplyRepository replyRepository;
	private final MemberRepository memberRepository;
	
	@Transactional(readOnly = true)
	public Page<ReplyDTO> findRepliesByType(ReplyType replyType, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Reply> repliesPage = replyRepository.findByType(replyType, pageable);
		
		Page<ReplyDTO> replies = repliesPage.map(m -> new ReplyDTO(m));

		return replies;
	}
	
	@Transactional(readOnly = true)
	public Page<GameListDTO> findGameReplyLike(ReplyType replyType,int page, int size){
		
		Pageable pageable = PageRequest.of(page, size);
	
		Page<GameReplyDTO> findGameAllReplyList = replyRepository.findReplyGame(replyType, pageable);
		
		Page<GameListDTO> replies = findGameAllReplyList.map(m -> new GameListDTO(m));
		
		return replies;
	}

	public void addReply(ReplyType replyType, String username, String content) {
		Member member = memberRepository.findByUsername(username).orElseThrow(
			() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Reply reply = Reply.builder()
			.member(member)
			.content(content)
			.type(replyType)
			.build();

		replyRepository.save(reply);
	}

	public void deleteReply(Long replyId, String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND));

		if (!reply.getMember().equals(member)) {
			throw new ReplyMemberInvalidException();
		}

		replyRepository.delete(reply);
	}



	
	
}
