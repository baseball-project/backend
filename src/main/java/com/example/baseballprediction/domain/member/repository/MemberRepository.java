package com.example.baseballprediction.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.baseballprediction.domain.member.dto.ProfileProjection;
import com.example.baseballprediction.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);

	Optional<Member> findByNickname(String nickname);

	@Query(
		"SELECT new com.example.baseballprediction.domain.member.dto.ProfileProjection(m.nickname, m.profileImageUrl, "
			+ "sum(case when f.type = 'WIN' then 1 else 0 end) as winFairyCount, "
			+ "sum(case when f.type = 'LOSE' then 1 else 0 end) as loseFairyCount) "
			+ "FROM Member m "
			+ "INNER JOIN MonthlyFairy f ON f.member = m "
			+ "WHERE m.id = :memberId "
			+ "GROUP BY m.nickname, m.profileImageUrl")
	Optional<ProfileProjection> findProfile(@Param("memberId") Long memberId);
}
