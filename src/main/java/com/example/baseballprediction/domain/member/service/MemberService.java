package com.example.baseballprediction.domain.member.service;

import static com.example.baseballprediction.domain.member.dto.MemberRequest.*;
import static com.example.baseballprediction.domain.member.dto.MemberResponse.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.dto.FairyProjection;
import com.example.baseballprediction.domain.member.dto.ProfileProjection;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.security.auth.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Transactional(readOnly = true)
	public Map<String, Object> login(String username, String password) {
		Member member = memberRepository.findByUsername(username).orElseThrow();

		if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
			throw new RuntimeException("패스워드가 일치하지 않습니다.");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("token", JwtTokenProvider.createToken(member));

		return response;
	}

	public void modifyLikeTeam(String username, int teamId) {
		Member member = memberRepository.findByUsername(username).orElseThrow();
		Team likeTeam = teamRepository.findById(teamId).orElseThrow();

		member.changeTeam(likeTeam);
	}

	public void modifyDetails(String username, DetailsDTO detailsDTO) {
		Member member = memberRepository.findByUsername(username).orElseThrow();

		if (isExistNickname(member.getNickname(), detailsDTO.getNickname())) {
			throw new RuntimeException("중복되는 닉네임이 있습니다.");
		}

		member.updateDetails(detailsDTO.getProfileImageUrl(), detailsDTO.getNickname(), detailsDTO.getComment());
	}

	public boolean isExistNickname(String originNickname, String nickname) {

		if (originNickname.equals(nickname))
			return false;

		return memberRepository.findByNickname(nickname).isPresent();
	}

	public ProfileProjection findProfile(Long memberId) {
		ProfileProjection profile = memberRepository.findProfile(memberId).orElseThrow();

		return profile;
	}

	public ProfileDTO findDetails(String username) {
		Member member = memberRepository.findByUsername(username).orElseThrow();

		ProfileDTO details = new ProfileDTO(member);

		return details;
	}

	public List<FairyProjection> findFairyStatistics(Long memberId) {
		List<FairyProjection> fairyProjections = memberRepository.findFairyStatistics(memberId);

		return fairyProjections;
	}
}
