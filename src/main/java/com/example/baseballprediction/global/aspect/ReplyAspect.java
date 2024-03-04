package com.example.baseballprediction.global.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.repository.ReplyReportRepository;
import com.example.baseballprediction.domain.reply.repository.ReplyRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class ReplyAspect {
	private final ReplyReportRepository replyReportRepository;
	private final ReplyRepository replyRepository;

	@AfterReturning("execution(* com.example.baseballprediction.domain.reply..*Service.addReplyReport(..))")
	@Transactional
	public void setStatusBlind(JoinPoint joinPoint) {
		Long replyId = (Long)joinPoint.getArgs()[0];

		Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new NotFoundException(
			ErrorCode.REPLY_NOT_FOUND));
		Long reportCount = replyReportRepository.countByReply(reply);

		if (reportCount < 5) {
			return;
		}

		reply.updateBlind();
	}
}
