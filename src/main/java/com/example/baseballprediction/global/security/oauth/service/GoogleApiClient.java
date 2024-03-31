package com.example.baseballprediction.global.security.oauth.service;

import java.io.UnsupportedEncodingException;

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
import com.example.baseballprediction.global.security.oauth.memberinfo.GoogleMemberInfo;
import com.example.baseballprediction.global.security.oauth.memberinfo.OAuth2MemberInfo;
import com.example.baseballprediction.global.security.oauth.token.GoogleToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleApiClient implements OAuth2ApiClient {
	@Value("${security.oauth2.client.registration.google.authorization-grant-type}")
	private String grantType;
	@Value("${security.oauth2.client.registration.google.client-id}")
	private String clientId;
	@Value("${security.oauth2.client.provider.google.token-uri}")
	private String tokenUri;
	@Value("${security.oauth2.client.provider.google.user-info-uri}")
	private String userInfoUri;
	@Value("${security.oauth2.client.registration.google.redirect-uri}")
	private String redirectUri;

	@Value("${security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;

	@Override
	public String requestAccessToken(String code) throws JsonProcessingException, UnsupportedEncodingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", grantType);
		body.add("client_id", clientId);
		body.add("redirect_uri", redirectUri);
		body.add("code", code);
		body.add("client_secret", clientSecret);

		ResponseEntity<String> codeEntity = Fetch.getTokenEntity(tokenUri, HttpMethod.POST, body);
		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

		GoogleToken googleToken = om.readValue(codeEntity.getBody(), GoogleToken.class);
		return googleToken.getAccessToken();
	}

	@Override
	public OAuth2MemberInfo requestMemberInfo(String code) {
		try {
			ResponseEntity<String> tokenEntity = Fetch.getMemberInfoEntityWithGoogle(userInfoUri, HttpMethod.GET,
				requestAccessToken(code));

			ObjectMapper om = new ObjectMapper();
			om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

			GoogleMemberInfo googleMemberInfo = null;
			googleMemberInfo = om.readValue(tokenEntity.getBody(), GoogleMemberInfo.class);
			return googleMemberInfo;
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
