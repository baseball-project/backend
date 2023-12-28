package com.example.baseballprediction.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageType {
	PROFILE("profiles"), TEAM("teams"), ETC("");

	private String folderName;
}
