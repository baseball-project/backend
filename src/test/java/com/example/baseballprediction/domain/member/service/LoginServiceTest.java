package com.example.baseballprediction.domain.member.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;

@ActiveProfiles("test")
@SpringBootTest
class LoginServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private LoginService loginService;

	@BeforeEach
	void setUp() {
		Member member = Member.builder()
			.username("playdot1")
			.password(bCryptPasswordEncoder.encode("password"))
			.nickname("테스트유저1")
			.socialType(SocialType.KAKAO)
			.build();

		Team team = Team.builder()
			.name("한화 이글스")
			.shortName("한화")
			.build();

		teamRepository.save(team);

		member.changeTeam(team);

		memberRepository.save(member);
	}

	@AfterEach
	void tearDown() {
		memberRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
	}

	@DisplayName("자체 로그인을 하여 토큰을 발급한다.")
	@Test
	void login() {
		//given
		String username = "playdot1";
		String password = "password";

		//when
		Map<String, Object> result = loginService.login(username, password);

		//then
		String resultToken = result.get("token").toString();
		resultToken = resultToken.replace(JwtTokenProvider.TOKEN_PREFIX, "");
		assertThat(JwtTokenProvider.validateToken(resultToken)).isTrue();
	}

	@DisplayName("자체 로그인을 하여 토큰을 발급한다. 아이디가 존재하지 않으면 예외가 발생한다.")
	@Test
	void loginWithNoUsername() {
		//given
		String username = "playdot2";
		String password = "password";

		//when
		//then
		assertThatThrownBy(() -> loginService.login(username, password))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
	}

	@DisplayName("자체 로그인을 하여 토큰을 발급한다. 비밀번호가 일치하지 않으면 예외가 발생한다.")
	@Test
	void loginWithInValidPassword() {
		//given
		String username = "playdot1";
		String password = "password11";

		//when
		//then
		assertThatThrownBy(() -> loginService.login(username, password))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ErrorCode.LOGIN_PASSWORD_INVALID.getMessage());
	}

	// 로그아웃 기능은 refresh token 작업후 진행한다.
	// @DisplayName("로그아웃을 한다. 기존 발급받은 토큰을 만료시킨다.")
	// @Test
	// void logout() {
	// 	//given
	// 	String username = "playdot1";
	// 	String password = "password";
	//
	// 	Map<String, Object> login = loginService.login(username, password);
	//
	// 	String loginToken = login.get("token").toString();
	// 	loginToken = loginToken.replace(JwtTokenProvider.TOKEN_PREFIX, "");
	//
	// 	//when
	// 	loginService.logout(loginToken);
	//
	// 	//then
	// 	String finalLoginToken = loginToken;
	// 	assertThatThrownBy(() -> JwtTokenProvider.validateToken(finalLoginToken))
	// 		.isInstanceOf(JwtException.class)
	// 		.hasMessage(ErrorCode.JWT_EXPIRED.getMessage());
	// }
}