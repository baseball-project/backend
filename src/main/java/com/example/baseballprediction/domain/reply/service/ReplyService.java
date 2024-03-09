package com.example.baseballprediction.domain.reply.service;

import static com.example.baseballprediction.domain.reply.dto.ReplyReportResponse.*;
import static com.example.baseballprediction.domain.reply.dto.ReplyResponse.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.reply.dto.ReplyLikeProjection;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.entity.ReplyReport;
import com.example.baseballprediction.domain.reply.repository.ReplyReportRepository;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.domain.reply.repository.ReplyRepositoryCustomImpl;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.constant.ReportType;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.error.exception.ReplyMemberInvalidException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {
	private final ReplyRepository replyRepository;
	private final MemberRepository memberRepository;

	private final ReplyRepositoryCustomImpl replyRepositoryCustom;

	private final ReplyReportRepository replyReportRepository;

	@Transactional(readOnly = true)
	public Page<ReplyDTO> findRepliesByType(ReplyType replyType, int page, int size, String username) {
		Pageable pageable = PageRequest.of(page, size);

		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Page<ReplyLikeProjection> replyProjections = replyRepositoryCustom.findAllRepliesByType(replyType, pageable, member.getId());

		List<ReplyLikeProjection> bestReplyLikeProjections = findBestReplies(replyProjections.getContent());

		List<ReplyLikeProjection> remainingReplies = remainReplies(replyProjections.getContent(), bestReplyLikeProjections);

		List<ReplyLikeProjection> replies = new ArrayList<>(bestReplyLikeProjections);
		replies.addAll(remainingReplies);

		Page<ReplyDTO> response = new PageImpl<>(replies.stream().map(ReplyDTO::new).collect(Collectors.toList()), pageable, replyProjections.getTotalElements());

		return response;
	}

	private List<ReplyLikeProjection> findBestReplies(List<ReplyLikeProjection> replyLikeProjections) {
		return replyLikeProjections.stream()
			.sorted(Comparator.comparingLong(ReplyLikeProjection::getCount).reversed())
			.limit(3)
			.collect(Collectors.toList());
	}

	private List<ReplyLikeProjection> remainReplies(List<ReplyLikeProjection> replyLikeProjections, List<ReplyLikeProjection> bestReplyLikeProjections) {
		return replyLikeProjections.stream()
			.filter(reply -> !bestReplyLikeProjections.contains(reply))
			.collect(Collectors.toList());
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

	public void addSubReply(Long replyId, ReplyType replyType, String username, String content) {
		Reply parentReply = replyRepository.findById(replyId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND));

		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Reply reply = Reply.builder()
			.member(member)
			.content(content)
			.type(replyType)
			.parentReply(parentReply)
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

	@Transactional(readOnly = true)
	public List<ReplyDTO> findSubReplies(Long parentReplyId) {
		Reply parentReply = replyRepository.findById(parentReplyId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND));

		List<ReplyLikeProjection> replies = replyRepository.findBySubReplies(parentReply);

		List<ReplyDTO> replyDTOList = replies.stream().map(m -> new ReplyDTO(m)).toList();

		return replyDTOList;
	}

	public List<ListDTO> findReportTypes() {
		return Arrays.stream(ReportType.values()).map(m -> new ListDTO(m.name(), m.getComment())).toList();
	}

	public void addReplyReport(Long replyId, String username, ReportType reportType) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REPLY_NOT_FOUND));

		if (isExistReport(member, reply)) {
			throw new BusinessException(ErrorCode.REPLY_REPORT_EXIST);
		}

		ReplyReport replyReport = ReplyReport.builder()
			.reply(reply)
			.member(member)
			.reportType(reportType)
			.build();

		replyReportRepository.save(replyReport);
	}

	private boolean isExistReport(Member member, Reply reply) {
		ReplyReport replyReport = replyReportRepository.findByMemberAndReply(member, reply);

		return replyReport != null;
	}
}
