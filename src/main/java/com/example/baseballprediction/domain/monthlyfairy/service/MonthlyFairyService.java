package com.example.baseballprediction.domain.monthlyfairy.service;

import static com.example.baseballprediction.domain.monthlyfairy.dto.MonthlyFairyResponse.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.domain.monthlyfairy.repository.MonthlyFairyRepository;
import com.example.baseballprediction.global.constant.FairyType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyFairyService {
	private final MonthlyFairyRepository monthlyFairyRepository;
	private final MemberRepository memberRepository;

	public StatisticsDTO findStatistics() {
		int currentMonth = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")).toString());
		List<MonthlyFairy> monthlyFairies = monthlyFairyRepository.findByMonth(currentMonth);

		List<FairyDTO> winFairies = new ArrayList<>();
		List<FairyDTO> loseFairies = new ArrayList<>();

		for (MonthlyFairy monthlyFairy : monthlyFairies) {
			findFairycount(monthlyFairy.getMember());
			FairyDTO fairyDTO = new FairyDTO(monthlyFairy);

			if (monthlyFairy.getType() == FairyType.WIN) {
				winFairies.add(fairyDTO);
				continue;
			}

			loseFairies.add(fairyDTO);
		}
		StatisticsDTO statistics = new StatisticsDTO(winFairies, loseFairies);

		return statistics;
	}

	private void findFairycount(Member member) {
		if (member == null) {
			throw new RuntimeException("멤버를 찾을 수 없습니다.");
		}

		List<MonthlyFairy> monthlyFairies = monthlyFairyRepository.findByMember(member);

		int winFairyCount = (int)monthlyFairies.stream()
			.filter(monthlyFairy -> monthlyFairy.getType() == FairyType.WIN)
			.count();

		int loseFairyCount = (int)monthlyFairies.stream()
			.filter(monthlyFairy -> monthlyFairy.getType() == FairyType.LOSE)
			.count();

		member.setFairyCount(winFairyCount, loseFairyCount);
	}
}
