package com.example.baseballprediction.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatGiftRequestDTO {

	private Long senderId;
	private Long recipientId;
	private int token;
}