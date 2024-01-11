package com.example.baseballprediction.domain.game.dto;

import java.time.LocalDateTime;
import org.springframework.data.annotation.LastModifiedDate;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GameReplyLikeProjection{
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GameListDTO{
		
		private profile profile;
		
		private reply reply;
		
		public GameListDTO(GameReplyDTO allReplyDTO) {
			this.profile = new profile(allReplyDTO.getProfileImageUrl(),allReplyDTO.getNickname(),allReplyDTO.getTeamName());
			this.reply = new reply(allReplyDTO.getId(),allReplyDTO.getContent(),allReplyDTO.getCount(),allReplyDTO.getCreatedAt());
		}
	}
	
	@Getter	
	@AllArgsConstructor
	public static class profile{
		private String profileImageUrl;
		private String nickname;
		private String teamName;
		
		public profile(Member member) {
			this.profileImageUrl= member.getProfileImageUrl();
			this.nickname = member.getNickname();
			this.teamName = member.getTeam().getName();
		}
		
	}
	
	@Getter	
	@AllArgsConstructor
	public static class reply{
		private Long replyId;
		private String content;
		private Long count;
		
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		@LastModifiedDate
		private LocalDateTime createdAt;
		
		public reply(Reply replyTest,Long count) {
			this.replyId = replyTest.getId();
			this.content = replyTest.getContent();
			this.count =  count;
			this.createdAt = replyTest.getCreatedAt();
		}

	}
	
}
