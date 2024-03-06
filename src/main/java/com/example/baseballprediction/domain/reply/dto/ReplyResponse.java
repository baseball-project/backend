package com.example.baseballprediction.domain.reply.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyResponse {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReplyDTO {
		private String profileImageUrl;
		private String nickname;
		private String teamName;
		private Long replyId;
		private String content;
		private Long likeCount;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		@LastModifiedDate
		private LocalDateTime createdAt;

		public ReplyDTO(ReplyLikeProjection replyLikeProjection) {
			this.profileImageUrl = replyLikeProjection.getProfileImageUrl();
			this.nickname = replyLikeProjection.getNickname();
			this.teamName = replyLikeProjection.getTeamName();
			this.replyId = replyLikeProjection.getId();
			this.content = replyLikeProjection.getContent();
			this.likeCount = replyLikeProjection.getCount();
			this.createdAt = replyLikeProjection.getCreatedAt();
		}
	}
}
