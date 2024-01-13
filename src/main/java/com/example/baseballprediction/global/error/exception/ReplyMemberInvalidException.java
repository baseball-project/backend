package com.example.baseballprediction.global.error.exception;

import com.example.baseballprediction.global.constant.ErrorCode;

public class ReplyMemberInvalidException extends BusinessException {
	public ReplyMemberInvalidException() {
		super(ErrorCode.REPLY_MEMBER_INVALID);
	}

	public ReplyMemberInvalidException(ErrorCode errorCode) {
		super(errorCode);
	}
}
