package com.example.baseballprediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BaseballPredictionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseballPredictionApplication.class, args);
	}

}
