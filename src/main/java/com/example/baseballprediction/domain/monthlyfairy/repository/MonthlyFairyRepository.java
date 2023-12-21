package com.example.baseballprediction.domain.monthlyfairy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;

public interface MonthlyFairyRepository extends JpaRepository<MonthlyFairy, Long> {
	List<MonthlyFairy> findByMonth(int month);

	List<MonthlyFairy> findByMember(Member member);
}
