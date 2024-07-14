package com.example.baseballprediction.global.security.oauth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import com.example.baseballprediction.global.security.oauth.dto.OAuthResponse;
import com.example.baseballprediction.global.security.oauth.memberinfo.OAuth2MemberInfo;

@ActiveProfiles("test")
@SpringBootTest
class OAuth2MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private KakaoApiClient kakaoApiClient;

	@Mock
	private NaverApiClient naverApiClient;

	@Mock
	private GoogleApiClient googleApiClient;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private OAuth2MemberService oAuth2MemberService;

	@DisplayName("카카오 로그인을 한다.")
	@Test
	void login() {
		//given
		String code = "kakaoCode";
		SocialType socialType = SocialType.KAKAO;
		OAuth2MemberInfo memberInfo = new OAuth2MemberInfo() {
			@Override
			public String getProviderId() {
				return "kakao";
			}

			@Override
			public SocialType getSocialType() {
				return SocialType.KAKAO;
			}

			@Override
			public String getEmail() {
				return "seonghye0n@kakao.com";
			}

			@Override
			public String generateNickname() {
				return "kakao_user";
			}
		};

		when(kakaoApiClient.requestMemberInfo(eq(code))).thenReturn(memberInfo);
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		given(JwtTokenProvider.createAccessToken(any(Member.class))).willReturn("mockToken");
		when(any(Member.class).getId()).thenReturn(anyLong());

		//when
		Map<String, Object> result = oAuth2MemberService.login(code, SocialType.KAKAO);

		//then
		assertThat("mockToken").isEqualTo(result.get("token"));
		OAuthResponse.LoginDTO loginDTO = (OAuthResponse.LoginDTO)result.get("body");
		assertThat(loginDTO).extracting("isNewMember", "profileImageUrl", "nickname", "teamName")
			.contains(true, null, "kakao_user", null);

	}
}