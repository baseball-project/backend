package com.example.baseballprediction.domain.gifttoken.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.member.entity.Member;

public interface GiftTokenRepository extends JpaRepository<GiftToken, Long> {
	Page<GiftToken> findByGiveMember(Member member, Pageable pageable);
}
