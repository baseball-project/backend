package com.example.baseballprediction.global.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
	READY("cont"), PROGRESS("ing"), END("end"), CANCEL("cancel");

	private final String name;

	public static Status findByName(String name) {
		return Arrays.stream(Status.values())
			.filter(status -> status.name.equals(name))
			.findFirst()
			.orElseThrow();
	}
}
