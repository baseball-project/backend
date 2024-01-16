package com.example.baseballprediction.global.error.exception;

import com.example.baseballprediction.global.constant.ErrorCode;

public class NotFoundException extends BusinessException {
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
