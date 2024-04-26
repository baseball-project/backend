package com.example.baseballprediction.domain.member.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.gifttoken.repository.GiftTokenRepository;
import com.example.baseballprediction.domain.member.dto.FairyProjection;
import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.domain.monthlyfairy.repository.MonthlyFairyRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

	private final MemberRepository memberRepository;
	private final MonthlyFairyRepository monthlyFairyRepository;
	private final GiftTokenRepository giftTokenRepository;

	@Transactional(readOnly = true)
	public List<FairyProjection> findFairyStatistics(Long memberId) {
		List<FairyProjection> fairyProjections = memberRepository.findFairyStatistics(memberId);

		return fairyProjections;
	}

	@Transactional(readOnly = true)
	public Page<MemberResponse.FairyHistoryDTO> findFairyHistories(Long memberId, int page, int list) {
		Pageable pageable = PageRequest.of(page, list);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Page<MonthlyFairy> monthlyFairies = monthlyFairyRepository.findByMemberToPage(member, pageable);

		Page<MemberResponse.FairyHistoryDTO> fairyHistories = monthlyFairies.map(
			m -> new MemberResponse.FairyHistoryDTO(member, m));

		return fairyHistories;
	}

	@Transactional(readOnly = true)
	public Page<MemberResponse.GiftHistoryDTO> findGiftHistories(Long memberId, int page, int list) {
		Member giveMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Pageable pageable = PageRequest.of(page, list);

		Page<GiftToken> giftTokens = giftTokenRepository.findByGiveMember(giveMember, pageable);

		Page<MemberResponse.GiftHistoryDTO> giftHistories = giftTokens.map(m -> new MemberResponse.GiftHistoryDTO(m));

		return giftHistories;
	}
}
