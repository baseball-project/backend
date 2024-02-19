package com.example.baseballprediction.domain.reply.repository;

import java.util.List;

import com.example.baseballprediction.domain.reply.entity.Reply;

public interface ReplyRepositoryCustom {
	List<Reply> findAllReplies();
}
