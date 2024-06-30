package com.example.baseballprediction.global.security.jwt.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.JwtException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import com.example.baseballprediction.global.security.jwt.entity.RefreshToken;
import com.example.baseballprediction.global.security.jwt.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class JwtService {
	private final JwtTokenProvider jwtTokenProvider;

	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	public Map<String, String> createNewToken(String refreshTokenId) {
		RefreshToken oldRefreshToken = refreshTokenRepository.findById(refreshTokenId)
			.orElseThrow(() -> new JwtException(ErrorCode.JWT_INVALID));

		Member member = memberRepository.findByUsername(
				JwtTokenProvider.getUsernameFromToken(oldRefreshToken.getToken()))
			.orElseThrow(() -> new NotFoundException(
				ErrorCode.MEMBER_NOT_FOUND));

		oldRefreshToken.invalidToken();

		String token = jwtTokenProvider.createRefreshToken(member);
		RefreshToken newRefreshToken = new RefreshToken(token);
		refreshTokenRepository.save(newRefreshToken);

		Map<String, String> response = new HashMap<>();
		response.put("refreshTokenId", newRefreshToken.getId());

		String accessToken = jwtTokenProvider.createAccessToken(member);

		response.put("accessToken", accessToken);

		return response;
	}
}
