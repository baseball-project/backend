package com.example.baseballprediction.domain.chat.dto;

import lombok.Getter;

public class ChatRequestDTO {
	
	@Getter
	public static class ChatGiftRequestDTO {
		private String recipientNickName;
		private int token;
		private String comment;
	}
	
	@Getter
	public static class ChatLeaveRequest {
		 private Long gameId;

	}
}
