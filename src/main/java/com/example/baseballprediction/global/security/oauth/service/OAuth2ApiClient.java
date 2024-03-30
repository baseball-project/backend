package com.example.baseballprediction.global.security.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface OAuth2ApiClient {
	String requestAccessToken(String code) throws JsonProcessingException;
}
