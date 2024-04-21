package com.example.baseballprediction.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRequestDTO {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
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
