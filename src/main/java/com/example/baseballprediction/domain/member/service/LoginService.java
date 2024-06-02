package com.example.baseballprediction.domain.member.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import com.example.baseballprediction.global.security.oauth.dto.OAuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final JwtTokenProvider jwtTokenProvider;

	public Map<String, Object> login(String username, String password) {
		Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(
			ErrorCode.MEMBER_NOT_FOUND));

		if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
			throw new BusinessException(ErrorCode.LOGIN_PASSWORD_INVALID);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("token", jwtTokenProvider.createToken(member));

		MemberResponse.LoginDTO body = new MemberResponse.LoginDTO(member);

		response.put("body", body);

		return response;
	}

	public void logout(String token) {
		jwtTokenProvider.expireToken(token);
	}

	public OAuthResponse.LoginDTO oauth2Login(String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		String teamName = member.getTeam() == null ? null : member.getTeam().getName();

		return new OAuthResponse.LoginDTO(member.isNewMember(), member.getProfileImageUrl(), member.getNickname(),
			teamName);
	}
}
