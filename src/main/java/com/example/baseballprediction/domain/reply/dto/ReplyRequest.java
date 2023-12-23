package com.example.baseballprediction.domain.reply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyRequest {
	@Getter
	@NoArgsConstructor
	public static class ReplyDTO {
		private String content;
	}
}
