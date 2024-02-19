package com.example.baseballprediction.domain.reply.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.baseballprediction.domain.member.entity.QMember;
import com.example.baseballprediction.domain.reply.dto.ReplyLikeProjection;
import com.example.baseballprediction.domain.reply.entity.QReply;
import com.example.baseballprediction.domain.replylike.entity.QReplyLike;
import com.example.baseballprediction.domain.team.entity.QTeam;
import com.example.baseballprediction.global.constant.ReplyType;
import com.example.baseballprediction.global.util.CustomDateUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReplyRepositoryCustomImpl {
	private final JPAQueryFactory queryFactory;

	public Page<ReplyLikeProjection> findAllRepliesByType(ReplyType replyType, Pageable pageable) {
		QReply reply = QReply.reply;
		QReplyLike replyLike = QReplyLike.replyLike;
		QMember member = QMember.member;
		QTeam team = QTeam.team;

		StringExpression dateCondition = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')",
			reply.createdAt);

		BooleanExpression whereCondition;
		if (replyType == ReplyType.FAIRY) {
			LocalDate aggregateDateStart = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
			LocalDate aggregateDateEnd = LocalDate.of(aggregateDateStart.getYear(),
				aggregateDateStart.getMonthValue(),
				aggregateDateStart.lengthOfMonth());

			whereCondition = dateCondition.between(CustomDateUtil.dateToString(aggregateDateStart),
				CustomDateUtil.dateToString(aggregateDateEnd));
		} else {
			whereCondition = dateCondition.eq(CustomDateUtil.dateToString(LocalDate.now()));
		}

		JPAQuery<ReplyLikeProjection> query = queryFactory.select(
				Projections.constructor(ReplyLikeProjection.class, reply.id, replyLike.id.count().as("count"),
					reply.createdAt,
					reply.content, member.profileImageUrl, member.nickname, team.name))
			.from(reply)
			.leftJoin(member).on(reply.member.id.eq(member.id))
			.leftJoin(team).on(reply.member.team.id.eq(team.id))
			.leftJoin(replyLike).on(replyLike.reply.id.eq(reply.id))
			.where(reply.type.eq(replyType), whereCondition, reply.parentReply.isNull())
			.groupBy(reply.id)
			.orderBy(reply.createdAt.desc());

		List<ReplyLikeProjection> projections = query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = query.fetchCount();

		return new PageImpl<>(projections, pageable, count);
	}
}
