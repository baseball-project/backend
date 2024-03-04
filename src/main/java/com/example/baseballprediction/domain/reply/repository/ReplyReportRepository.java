package com.example.baseballprediction.domain.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.reply.entity.ReplyReport;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {
	Long countByReply(Reply reply);

	ReplyReport findByMemberAndReply(Member member, Reply reply);
}
