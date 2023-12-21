package com.example.baseballprediction.domain.reply.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.global.constant.ReplyType;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	Page<Reply> findByType(ReplyType type, Pageable pageable);
}
