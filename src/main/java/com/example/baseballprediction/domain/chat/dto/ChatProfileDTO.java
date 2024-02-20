package com.example.baseballprediction.domain.chat.dto;


import com.example.baseballprediction.domain.team.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatProfileDTO {
	
		private String nickname;
		private String profileImageUrl;
		private String TeamName;
		
		public ChatProfileDTO(String nickname,String profileImageUrl,Team team) {
			this.nickname = nickname;
			this.profileImageUrl = profileImageUrl;
			this.TeamName = team.getName();
		}
		

}
