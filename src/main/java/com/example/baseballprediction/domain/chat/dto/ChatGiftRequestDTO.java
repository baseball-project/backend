package com.example.baseballprediction.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatGiftRequestDTO {
	private String recipientNickName;
	private int token;
}
