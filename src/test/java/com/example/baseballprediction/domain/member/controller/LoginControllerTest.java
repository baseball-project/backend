package com.example.baseballprediction.domain.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTest {

	@LocalServerPort
	private int port;

	@DisplayName("자체 로그인을 한다.")
	@Test
	void login() {
		//given

		//when

		//then
	}

	@DisplayName("카카오 로그인을 한다.")
	@Test
	void loginWithOAuth2WithKakao() {
		//given
		
		//when

		//then
	}
}