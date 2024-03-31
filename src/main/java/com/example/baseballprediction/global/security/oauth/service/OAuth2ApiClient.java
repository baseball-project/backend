package com.example.baseballprediction.global.security.oauth.service;

import java.io.UnsupportedEncodingException;

import com.example.baseballprediction.global.security.oauth.memberinfo.OAuth2MemberInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface OAuth2ApiClient {
	String requestAccessToken(String code) throws JsonProcessingException, UnsupportedEncodingException;

	OAuth2MemberInfo requestMemberInfo(String code);
}
