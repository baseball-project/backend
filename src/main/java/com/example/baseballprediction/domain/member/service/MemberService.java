package com.example.baseballprediction.domain.member.service;

import static com.example.baseballprediction.domain.member.dto.MemberRequest.*;
import static com.example.baseballprediction.domain.member.dto.MemberResponse.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.gifttoken.repository.GiftTokenRepository;
import com.example.baseballprediction.domain.member.dto.FairyProjection;
import com.example.baseballprediction.domain.member.dto.ProfileProjection;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.domain.monthlyfairy.repository.MonthlyFairyRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.ImageType;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.security.auth.JwtTokenProvider;
import com.example.baseballprediction.global.util.S3UploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final MonthlyFairyRepository monthlyFairyRepository;
	private final GiftTokenRepository giftTokenRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final S3UploadService s3UploadService;

	@Transactional(readOnly = true)
	public Map<String, Object> login(String username, String password) {
		Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(
			ErrorCode.MEMBER_NOT_FOUND));

		if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
			throw new BusinessException(ErrorCode.LOGIN_PASSWORD_INVALID);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("token", JwtTokenProvider.createToken(member));

		return response;
	}

	public void modifyLikeTeam(String username, int teamId) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
		Team likeTeam = teamRepository.findById(teamId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

		member.changeTeam(likeTeam);
	}

	public void modifyDetails(String username, DetailsDTO detailsDTO, MultipartFile profileImage) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		if (isExistNickname(member.getNickname(), detailsDTO.getNickname())) {
			throw new BusinessException(ErrorCode.MEMBER_NICKNAME_EXIST);
		}

		String uploadFileName = s3UploadService.updateFile(profileImage, member.getProfileImageUrl(),
			ImageType.PROFILE);

		member.updateDetails(uploadFileName, detailsDTO.getNickname(), detailsDTO.getComment());
	}

	@Transactional(readOnly = true)
	private boolean isExistNickname(String originNickname, String nickname) {

		if (originNickname.equals(nickname))
			return false;

		return memberRepository.findByNickname(nickname).isPresent();
	}

	@Transactional(readOnly = true)
	public ProfileProjection findProfile(Long memberId) {
		ProfileProjection profile = memberRepository.findProfile(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		return profile;
	}

	@Transactional(readOnly = true)
	public ProfileDTO findDetails(String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		ProfileDTO details = new ProfileDTO(member);

		return details;
	}

	@Transactional(readOnly = true)
	public List<FairyProjection> findFairyStatistics(Long memberId) {
		List<FairyProjection> fairyProjections = memberRepository.findFairyStatistics(memberId);

		return fairyProjections;
	}

	@Transactional(readOnly = true)
	public Page<FairyHistoryDTO> findFairyHistories(Long memberId, int page, int list) {
		Pageable pageable = PageRequest.of(page, list);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Page<MonthlyFairy> monthlyFairies = monthlyFairyRepository.findByMemberToPage(member, pageable);

		Page<FairyHistoryDTO> fairyHistories = monthlyFairies.map(m -> new FairyHistoryDTO(member, m));

		return fairyHistories;
	}

	@Transactional(readOnly = true)
	public Page<GiftHistoryDTO> findGiftHistories(Long memberId, int page, int list) {
		Member giveMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Pageable pageable = PageRequest.of(page, list);

		Page<GiftToken> giftTokens = giftTokenRepository.findByGiveMember(giveMember, pageable);

		Page<GiftHistoryDTO> giftHistories = giftTokens.map(m -> new GiftHistoryDTO(m));

		return giftHistories;
	}
}
