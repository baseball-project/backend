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
import com.example.baseballprediction.global.security.jwt.entity.RefreshToken;
import com.example.baseballprediction.global.security.jwt.repository.RefreshTokenRepository;
import com.example.baseballprediction.global.security.oauth.dto.OAuthResponse;
import com.example.baseballprediction.global.security.oauth.memberinfo.OAuth2MemberInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService {
	private final MemberRepository memberRepository;
	private final KakaoApiClient kakaoApiClient;
	private final NaverApiClient naverApiClient;
	private final GoogleApiClient googleApiClient;

	private final JwtTokenProvider jwtTokenProvider;

	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public Map<String, Object> login(String code, SocialType socialType) {
		OAuth2MemberInfo oAuth2MemberInfo = null;

		if (socialType == SocialType.KAKAO) {
			oAuth2MemberInfo = kakaoApiClient.requestMemberInfo(code);
		} else if (socialType == SocialType.NAVER) {
			oAuth2MemberInfo = naverApiClient.requestMemberInfo(code);
		} else if (socialType == SocialType.GOOGLE) {
			oAuth2MemberInfo = googleApiClient.requestMemberInfo(code);
		}

		Member member = createOrFindMember(oAuth2MemberInfo.getEmail(), oAuth2MemberInfo.getSocialType(),
			oAuth2MemberInfo.generateNickname());

		Map<String, Object> response = new HashMap<>();

		String token = jwtTokenProvider.createRefreshToken(member);
		RefreshToken refreshToken = new RefreshToken(token);
		refreshTokenRepository.save(refreshToken);

		response.put("refreshTokenId", refreshToken.getId());

		String accessToken = jwtTokenProvider.createAccessToken(member);

		OAuthResponse.LoginDTO loginDTO = new OAuthResponse.LoginDTO(member, accessToken);
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
