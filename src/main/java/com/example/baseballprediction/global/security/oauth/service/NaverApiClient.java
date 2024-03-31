package com.example.baseballprediction.global.security.oauth.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.security.oauth.memberinfo.NaverMemberInfo;
import com.example.baseballprediction.global.security.oauth.memberinfo.OAuth2MemberInfo;
import com.example.baseballprediction.global.security.oauth.token.NaverToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NaverApiClient implements OAuth2ApiClient {
	@Value("${security.oauth2.client.registration.naver.authorization-grant-type}")
	private String grantType;
	@Value("${security.oauth2.client.registration.naver.client-id}")
	private String clientId;
	@Value("${security.oauth2.client.provider.naver.token-uri}")
	private String tokenUri;
	@Value("${security.oauth2.client.provider.naver.user-info-uri}")
	private String userInfoUri;
	@Value("${security.oauth2.client.registration.naver.redirect-uri}")
	private String redirectUri;

	@Value("${security.oauth2.client.registration.naver.client-secret}")
	private String clientSecret;

	@Value("${security.oauth2.client.registration.naver.state}")
	private String state;

	@Override
	public String requestAccessToken(String code) throws JsonProcessingException, UnsupportedEncodingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", grantType);
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("state", URLEncoder.encode(state, "UTF-8"));
		body.add("code", code);

		ResponseEntity<String> codeEntity = Fetch.getTokenEntity(tokenUri, HttpMethod.POST, body);
		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

		NaverToken naverToken = om.readValue(codeEntity.getBody(), NaverToken.class);

		return naverToken.getAccessToken();
	}

	@Override
	public OAuth2MemberInfo requestMemberInfo(String code) {
		try {
			ResponseEntity<String> tokenEntity = Fetch.getMemberInfoEntity(userInfoUri, HttpMethod.POST,
				requestAccessToken(code));

			ObjectMapper om = new ObjectMapper();
			om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

			NaverMemberInfo naverMemberInfo = null;
			naverMemberInfo = om.readValue(tokenEntity.getBody(), NaverMemberInfo.class);

			return naverMemberInfo;
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
