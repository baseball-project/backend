package com.example.baseballprediction.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileProjection {
	private String nickname;
	private String profileImageUrl;
	private Long winFairyCount;
	private Long loseFairyCount;

	private String teamName;
	private String comment;
}
