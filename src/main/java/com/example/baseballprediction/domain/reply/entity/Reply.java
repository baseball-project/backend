package com.example.baseballprediction.domain.reply.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.replylike.entity.ReplyLike;
import com.example.baseballprediction.global.constant.ReplyStatus;
import com.example.baseballprediction.global.constant.ReplyType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reply_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_reply_id")
	private Reply parentReply;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false, length = 1000)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(name = "reply_type")
	private ReplyType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "reply_status")
	private ReplyStatus status;

	@OneToMany(mappedBy = "reply", cascade = CascadeType.REMOVE)
	private List<ReplyLike> replyLikes = new ArrayList<>();

	@Builder
	public Reply(Member member, String content, ReplyType type, Reply parentReply) {
		this.member = member;
		this.content = content;
		this.type = type;
		this.parentReply = parentReply;
		this.status = ReplyStatus.NORMAL;
	}

	public void updateBlind() {
		this.status = ReplyStatus.BLIND;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Reply reply = (Reply)o;
		return Objects.equals(id, reply.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}