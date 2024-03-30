package com.example.baseballprediction.global.security.oauth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import com.example.baseballprediction.global.security.oauth.dto.OAuthResponse;
import com.example.baseballprediction.global.security.oauth.memberinfo.KakaoMemberInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService {
	private final MemberRepository memberRepository;
	private final KakaoApiClient kakaoApiClient;

	@Transactional(readOnly = true)
	public Map<String, Object> login(String code) throws JsonProcessingException {
		KakaoMemberInfo kakaoMemberInfo = kakaoApiClient.requestMemberInfo(code);

		Member member = createOrFindMember(kakaoMemberInfo.getEmail(), kakaoMemberInfo.getSocialType(),
			"KAKAO_" + kakaoMemberInfo.getId());

		Map<String, Object> response = new HashMap<>();
		response.put("token", JwtTokenProvider.createToken(member));

		OAuthResponse.LoginDTO loginDTO = new OAuthResponse.LoginDTO(member);
		response.put("body", loginDTO);

		return response;
	}

	@Transactional
	private Member createOrFindMember(String username, SocialType socialType, String nickname) {
		Optional<Member> findMember = memberRepository.findByUsername(username);

		if (findMember.isEmpty()) {
			return createMember(username, socialType, nickname);
		}

		Member member = findMember.get();
		member.setIsNewMember(false);

		return member;
	}

	@Transactional
	private Member createMember(String username, SocialType socialType, String nickname) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String password = bCryptPasswordEncoder.encode("password");
		Member member = Member.builder()
			.username(username)
			.socialType(socialType)
			.nickname(nickname)
			.password(password)
			.isNewMember(true)
			.build();

		memberRepository.save(member);

		return member;
	}
}
