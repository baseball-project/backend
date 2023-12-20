package com.example.baseballprediction.domain.monthlyfairy.entity;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.global.constant.FairyType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyFairy {
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
