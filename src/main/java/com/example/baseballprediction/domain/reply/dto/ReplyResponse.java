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
		private ProfileDTO profile;
		private ReplyDetailsDTO reply;

		public ReplyDTO(ReplyLikeProjection replyLikeProjection) {
			this.profile = new ProfileDTO(replyLikeProjection.getProfileImageUrl(), replyLikeProjection.getNickname(),
				replyLikeProjection.getTeamName());
			this.reply = new ReplyDetailsDTO(replyLikeProjection.getId(), replyLikeProjection.getContent(),
				replyLikeProjection.getCount(), replyLikeProjection.getCreatedAt());
		}
	}

	@Getter
	@AllArgsConstructor
	public static class ProfileDTO {
		private String profileImageUrl;
		private String nickname;
		private String teamName;
	}

	@Getter
	@AllArgsConstructor
	public static class ReplyDetailsDTO {
		private Long replyId;
		private String content;
		private Long count;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		@LastModifiedDate
		private LocalDateTime createdAt;
	}
}
