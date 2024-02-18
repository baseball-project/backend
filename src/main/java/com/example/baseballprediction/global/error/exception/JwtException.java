package com.example.baseballprediction.global.error.exception;

import com.example.baseballprediction.global.constant.ErrorCode;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtException extends BusinessException {
	public JwtException(ErrorCode errorCode) {
		super(errorCode);
	}
}
