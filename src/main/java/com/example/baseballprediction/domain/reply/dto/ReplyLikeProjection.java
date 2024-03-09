package com.example.baseballprediction.domain.reply.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLikeProjection {

	private Long id;

	private Long count;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	@LastModifiedDate
	private LocalDateTime createdAt;

	private String content;

	private String profileImageUrl;

	private String nickname;

	private String teamName;
	private boolean isLiked;

	public ReplyLikeProjection(Long id, Long count, String content, LocalDateTime createdAt, String profileImageUrl,
		String nickname, String teamName, boolean isLiked) {
		this.id = id;
		this.count = count;
		this.content = content;
		this.createdAt = createdAt;
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.teamName = teamName;
		this.isLiked = isLiked;
	}

}
