package com.example.baseballprediction.domain.reply.service;

import static com.example.baseballprediction.domain.reply.dto.ReplyResponse.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.global.constant.ReplyType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {
	private final ReplyRepository replyRepository;

	public Page<ReplyDTO> findRepliesByType(ReplyType replyType, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Reply> repliesPage = replyRepository.findByType(replyType, pageable);

		Page<ReplyDTO> replies = repliesPage.map(m -> new ReplyDTO(m));

		return replies;
	}
}
