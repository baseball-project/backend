package com.example.baseballprediction.domain.chat.dto;

import lombok.Getter;

@Getter
public class ChatLeaveMessage {
	
	private String nickname;
    private String message;

    public ChatLeaveMessage(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
    }
	
}
