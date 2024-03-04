package com.example.baseballprediction.domain.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyReportResponse {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ListDTO {
		private String type;
		private String comment;
	}
}
