package com.example.baseballprediction.global.constant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "로그인 후 이용해주세요."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
	LOGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "로그인에 실패했습니다. 다시 시도해주세요."),
	MEMBER_NICKNAME_EXIST(HttpStatus.BAD_REQUEST, "중복되는 닉네임입니다."),
	MEMBER_NICKNAME_LENGTH(HttpStatus.BAD_REQUEST, "닉네임은 20자 이하로 입력해주세요."),
	MEMBER_NICKNAME_NULL(HttpStatus.BAD_REQUEST, "닉네임은 공백일 수 없습니다."),
	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자 정보를 찾을 수 없습니다."),
	TEAM_NOT_FOUND(HttpStatus.BAD_REQUEST, "팀 정보를 찾을 수 없습니다."),
	REPLY_NOT_FOUND(HttpStatus.BAD_REQUEST, "댓글 정보를 찾을 수 없습니다."),
	REPLY_LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "좋아요 정보를 찾을 수 없습니다."),
	REPLY_MEMBER_INVALID(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 수정/삭제가 가능합니다."),
	LOGIN_PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	REPLY_LIKE_MEMBER_INVALID(HttpStatus.FORBIDDEN, "본인이 누른 좋아요만 취소할 수 있습니다."),
	REPLY_LIKE_DUPLICATED(HttpStatus.BAD_REQUEST, "좋아요는 한 번만 누를 수 있습니다."),
	JWT_INVALID(HttpStatus.BAD_REQUEST, "인증 정보가 올바르지 않습니다."),
	JWT_EXPIRED(HttpStatus.FORBIDDEN, "인증 정보가 만료되었습니다."),
	JWT_NOT_MATCHED(HttpStatus.BAD_REQUEST, "인증 정보가 일치하지 않습니다."),
	GIFTING_TO_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신에게 선물할 수 없습니다."),
	INSUFFICIENT_TOKENS(HttpStatus.UNPROCESSABLE_ENTITY, "선물할 토큰이 부족합니다."),
	REPLY_REPORT_EXIST(HttpStatus.BAD_REQUEST, "이미 신고한 댓글입니다."),
	MINI_GAME_TOKENS_INSUFFICIENT(HttpStatus.BAD_REQUEST, "미니게임을 만들 토큰이 부족합니다."),
    MINI_GAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "미니게임이 존재하지 않습니다."),
    MINI_GAME_NOT_PARTICIPATED(HttpStatus.BAD_REQUEST, "투표에 참여하지 않았습니다."),
    GAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "게임 정보를 찾을 수 없습니다."),
	MINI_GAME_MAX_VOTE_LIMIT(HttpStatus.BAD_REQUEST, "더 이상 미니게임을 만들 수 없습니다."),
	MINI_GAME_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "이미 종료된 미니투표 입니다."),
    MINI_GAME_CURRENTLY_WAITING(HttpStatus.BAD_REQUEST, "현재 대기상태중인 미니투표입니다."),
	GAMEID_OR_AUTH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "gameId 및 인증 토큰 값이 없습니다."),
	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "인증 토큰이 유효하지 않습니다."),
	GAME_OR_CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "게임이 존재하지 않거나 채팅방에 입장 할 수 없습니다."),
	VOTING_REQUIRED_FOR_ENTRY(HttpStatus.BAD_REQUEST, "투표 완료 후 채팅방에 입장 할 수 있습니다.");
	
	private final HttpStatus httpStatus;
	private final String message;

	public int getStatusCode() {
		return httpStatus.value();
	}
}
