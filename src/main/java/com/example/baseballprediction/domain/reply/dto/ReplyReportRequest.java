package com.example.baseballprediction.domain.reply.dto;

import com.example.baseballprediction.global.constant.ReportType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyReportRequest {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReportDTO {
		private ReportType reportType;
	}
}
