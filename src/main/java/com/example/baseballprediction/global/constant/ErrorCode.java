package com.example.baseballprediction.global.constant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "로그인 후 이용해주세요."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "오류가 발생했습니다. 잠시 후 다시 시도해주세요."),

	DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "중복되는 닉네임입니다."),
	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자 정보를 찾을 수 없습니다."),
	REPLY_NOT_FOUND(HttpStatus.BAD_REQUEST, "댓글 정보를 찾을 수 없습니다."),
	REPLY_MEMBER_INVALID(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 수정/삭제가 가능합니다.");

	private final HttpStatus httpStatus;
	private final String message;

	public int getStatusCode() {
		return httpStatus.value();
	}
}
