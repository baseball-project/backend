package com.example.baseballprediction.domain.chat.dto;

import com.example.baseballprediction.global.constant.ChatType;
import lombok.Getter;

@Getter
public class ChatMessage {

    private ChatType type;
    private Long gameId;
    private String message;
    private String nickname;
    private String profileImageUrl;
    private String teamName;
    
    public void SendMessage(String message) {
    	setMessage(message);
    }
    
    private void setMessage(String message) {
    	this.message = message;
    }
    
    public void SendProfile(String nickname,String profileImageUrl,String teamName) {
    	setProfile(nickname,profileImageUrl,teamName);
    }
    
    private void setProfile(String nickname,String profileImageUrl,String teamName) {
    	this.nickname = nickname;
    	this.profileImageUrl = profileImageUrl;
    	this.teamName = teamName;
    }
}