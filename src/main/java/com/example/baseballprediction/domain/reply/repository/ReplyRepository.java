package com.example.baseballprediction.domain.reply.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.baseballprediction.domain.reply.dto.ReplyLikeProjection;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.global.constant.ReplyType;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
	Page<Reply> findByType(ReplyType type, Pageable pageable);

	@Query(
		"SELECT "
			+ " new com.example.baseballprediction.domain.reply.dto.ReplyLikeProjection(r.id ,"
			+ " count(rl.id) as count,"
			+ " r.createdAt,"
			+ " r.content,"
			+ " m.profileImageUrl,"
			+ " m.nickname,"
			+ " t.name)"
			+ " from Reply r "
			+ " LEFT JOIN ReplyLike rl"
			+ " ON rl.reply.id = r.id "
			+ " LEFT JOIN Member m "
			+ "	ON m.id = r.member.id"
			+ "	LEFT JOIN team t"
			+ "	ON t.id = m.team.id"
			+ "	WHERE r.type =:replyType"
			+ "	AND date(r.createdAt) = date(now())"
			+ " AND r.parentReply IS NULL"
			+ " group by r.id"
			+ "	order by  r.createdAt desc")
	Page<ReplyLikeProjection> findReplyGame(@Param("replyType") ReplyType replyType, Pageable pageable);

	@Query(
		"SELECT "
			+ " new com.example.baseballprediction.domain.reply.dto.ReplyLikeProjection(r.id ,"
			+ " count(rl.id) as count,"
			+ " r.createdAt,"
			+ " r.content,"
			+ " m.profileImageUrl,"
			+ " m.nickname,"
			+ " t.name,"
			+ " CASE WHEN rl.member IS NOT NULL THEN true ELSE false END as isLiked)"
			+ " from Reply r "
			+ " LEFT JOIN ReplyLike rl"
			+ " ON rl.reply.id = r.id AND rl.member.id = :memberId "
			+ " LEFT JOIN Member m "
			+ "	ON m.id = r.member.id"
			+ "	LEFT JOIN team t"
			+ "	ON t.id = m.team.id"
			+ "	WHERE r.parentReply = :parentReply"
			+ "	AND date(r.createdAt) = date(now())"
			+ " group by r.id"
			+ "	order by  r.createdAt desc")
	List<ReplyLikeProjection> findBySubReplies(Reply parentReply, Long memberId);
}
