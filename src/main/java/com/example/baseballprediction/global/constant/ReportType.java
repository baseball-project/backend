package com.example.baseballprediction.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ReportType {
	ABUSE("부적절한 언어 사용"),
	SPAM("스팸/홍 및 도배글"),
	PRIVACY("개인정보 노출"),
	ETC("기타");

	private String comment;
}
