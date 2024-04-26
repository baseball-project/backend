package com.example.baseballprediction.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageType {
	ENTER_MESSAGE("님이 입장하셨습니다."),
	VOTE_STARTED("투표가 시작되었습니다."),
	VOTE_CREATED("투표가 생성됐습니다. 잠시만 기다려주세요."),
	THANK_YOU_FOR_VOTING("투표해주셔서 감사합니다."),
	ALREADY_VOTED("이미 투표하셨습니다."),
	LEAVE_MESSAGE("님이 채팅방을 떠났습니다.");
	
    private final String message;
   
    @Override
    public String toString() {
        return message;
    }
    
    public static String getEnterMessage(ChatMessageType chatMessageType, String username) {
    	return username + chatMessageType;
    }
    
}
