package com.example.baseballprediction.domain.chat.dto;

import lombok.Getter;

@Getter
public class ChatRoom {
	
	private Long gameId;
	private String home;
	private String away;
	
	public static ChatRoom create(Long gameId,String home,String away) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.gameId =  gameId;
		chatRoom.home = home;
		chatRoom.away = away;
		return chatRoom;
	}
	
}
