package com.example.baseballprediction.domain.chat.dto;

import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.ChatType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatEventDTO {
	
	@Getter
	public static class ChatLeaveMessage {
		
		private String nickname;
	    private String message;

	    public ChatLeaveMessage(String nickname, String message) {
	        this.nickname = nickname;
	        this.message = message;
	    }
		
	}
	
	@Getter
	public static class ChatMessage {

		private ChatType type;
		private Long gameId;
		private String message;
		private ChatProfileDTO profile;
		private String teamType;
		
		public void setMessage(String message) {
			this.message = message;
		}
		
		public void sendProfile( ChatProfileDTO chatProfileDTO) {
			setProfile(chatProfileDTO);
		}
		
		private void setProfile(ChatProfileDTO chatProfileDTO) {
			this.profile = new ChatProfileDTO(chatProfileDTO.getNickname(),chatProfileDTO.getProfileImageUrl(),chatProfileDTO.getTeamName());
		}
		
		public void setTeamType(String teamType) {
			this.teamType = teamType;
			
		}
	}
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatProfileDTO {
			private String nickname;
			private String profileImageUrl;
			private String TeamName;
			
			public ChatProfileDTO(String nickname,String profileImageUrl,Team team) {
				this.nickname = nickname;
				this.profileImageUrl = profileImageUrl;
				this.TeamName = team.getName();
			}
			

	}

}
