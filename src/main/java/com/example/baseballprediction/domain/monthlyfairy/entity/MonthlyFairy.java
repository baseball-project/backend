package com.example.baseballprediction.domain.monthlyfairy.entity;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.global.constant.FairyType;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyFairy extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "monthly_fairy_id")
	private Long id;

	@Column(name = "statistic_month", nullable = false)
	private int month;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private FairyType type;

	@Column(name = "fairy_rank", nullable = false)
	private int rank;

	@Column(nullable = false)
	private int voteRatio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public MonthlyFairy(int month, FairyType type, int rank, int voteRatio, Member member) {
		this.month = month;
		this.type = type;
		this.rank = rank;
		this.voteRatio = voteRatio;
		this.member = member;
	}
}
