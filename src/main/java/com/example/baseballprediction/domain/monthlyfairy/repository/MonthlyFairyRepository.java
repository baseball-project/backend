package com.example.baseballprediction.domain.monthlyfairy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;

public interface MonthlyFairyRepository extends JpaRepository<MonthlyFairy, Long> {
	@Query("SELECT f FROM MonthlyFairy f"
		+ " JOIN FETCH f.member m"
		+ " WHERE f.month = :month")
	List<MonthlyFairy> findByMonth(int month);

	List<MonthlyFairy> findByMember(Member member);

	@Query("SELECT m FROM MonthlyFairy m WHERE m.member = :member")
	Page<MonthlyFairy> findByMemberToPage(Member member, Pageable pageable);
}
