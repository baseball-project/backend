package com.example.baseballprediction.domain.member.service;

import org.springframework.stereotype.Component;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.gifttoken.repository.GiftTokenRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;

@Component
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class MemberServiceHelper {
	private final MemberRepository memberRepository;
	private final GiftTokenRepository giftTokenRepository;

	public MemberServiceHelper(final MemberRepository memberRepository, GiftTokenRepository giftTokenRepository) {
		this.memberRepository = memberRepository;
		this.giftTokenRepository = giftTokenRepository;
	}

	@Transactional(readOnly = true)
	public Team getFavoriteTeam(final Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow();
		member.getTeam().getName();
		return member.getTeam();
	}

	@Transactional(readOnly = true)
	public Member getTakeMember(final Long id) {
		GiftToken giftToken = giftTokenRepository.findById(id).orElseThrow();
		giftToken.getTakeMember().getNickname();

		return giftToken.getTakeMember();
	}

	@Transactional(readOnly = true)
	public Member getGiveMember(final Long id) {
		GiftToken giftToken = giftTokenRepository.findById(id).orElseThrow();
		giftToken.getGiveMember().getNickname();

		return giftToken.getGiveMember();
	}

	@Transactional
	public Member getMember(final Long id) {
		Member member = memberRepository.findById(id).orElseThrow();

		return member;
	}
}
