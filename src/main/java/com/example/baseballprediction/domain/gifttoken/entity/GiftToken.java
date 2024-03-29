package com.example.baseballprediction.domain.gifttoken.entity;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "gift_token_log")
public class GiftToken extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gift_token_log_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "take_member_id")
	private Member takeMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "give_member_id")
	private Member giveMember;

	@Column(nullable = false)
	private int tokenAmount;

	@Column(length = 100)
	private String comment;

	@Builder
	public GiftToken(Member takeMember, Member giveMember, int tokenAmount, String comment) {
		this.takeMember = takeMember;
		this.giveMember = giveMember;
		this.tokenAmount = tokenAmount;
		this.comment = comment;
	}

}
