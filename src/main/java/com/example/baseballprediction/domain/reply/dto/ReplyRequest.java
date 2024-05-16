package com.example.baseballprediction.domain.reply.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyRequest {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReplyDTO {
		@NotBlank(message = "댓글을 입력해주세요.")
		private String content;
	}
}
