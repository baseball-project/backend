package com.example.baseballprediction.domain.chat.dto;

import com.example.baseballprediction.global.constant.ChatType;
import lombok.Getter;

@Getter
public class ChatMessage {

    private ChatType type;
    private Long gameId;
    private String message;
    private ChatProfileDTO profile;
    
    public void setMessage(String message) {
    	this.message = message;
    }
    
    public void SendProfile( ChatProfileDTO chatProfileDTO) {
    	setProfile(chatProfileDTO);
    }
    
    private void setProfile(ChatProfileDTO chatProfileDTO) {
    	this.profile = new ChatProfileDTO(chatProfileDTO.getNickname(),chatProfileDTO.getProfileImageUrl(),chatProfileDTO.getTeamName());
    }
}