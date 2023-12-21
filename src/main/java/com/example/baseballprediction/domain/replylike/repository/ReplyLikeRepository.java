package com.example.baseballprediction.domain.replylike.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.replylike.entity.ReplyLike;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
	Long countByReply(Reply reply);

	Optional<ReplyLike> findByReply(Reply reply);
}
