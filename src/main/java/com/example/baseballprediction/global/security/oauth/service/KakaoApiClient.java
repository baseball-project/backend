package com.example.baseballprediction.global.security.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.baseballprediction.global.security.oauth.memberinfo.KakaoMemberInfo;
import com.example.baseballprediction.global.security.oauth.token.KakaoToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuth2ApiClient {
	@Value("${security.oauth2.client.registration.kakao.authorization-grant-type}")
	private String grantType;
	@Value("${security.oauth2.client.registration.kakao.client-id}")
	private String clientId;
	@Value("${security.oauth2.client.provider.kakao.token-uri}")
	private String tokenUri;
	@Value("${security.oauth2.client.provider.kakao.user-info-uri}")
	private String userInfoUri;
	@Value("${security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;

	@Value("${security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;

	@Override
	public String requestAccessToken(String code) throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", grantType);
		body.add("client_id", clientId);
		body.add("redirect_uri", redirectUri);
		body.add("code", code);
		body.add("client_secret", clientSecret);

		ResponseEntity<String> codeEntity = Fetch.kakao(tokenUri, HttpMethod.POST, body);
		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

		KakaoToken kakaoToken = om.readValue(codeEntity.getBody(), KakaoToken.class);

		return kakaoToken.getAccessToken();
	}

	public KakaoMemberInfo requestMemberInfo(String code) throws JsonProcessingException {
		ResponseEntity<String> tokenEntity = Fetch.kakao(userInfoUri, HttpMethod.POST,
			requestAccessToken(code));

		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

		KakaoMemberInfo kakaoMemberInfo = om.readValue(tokenEntity.getBody(), KakaoMemberInfo.class);

		return kakaoMemberInfo;
	}
}
