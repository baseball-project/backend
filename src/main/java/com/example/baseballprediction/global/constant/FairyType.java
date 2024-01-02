package com.example.baseballprediction.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FairyType {
	WIN("승리요정"), LOSE("패배요정");

	private String name;
}
