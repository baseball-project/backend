package com.example.baseballprediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BaseballPredictionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseballPredictionApplication.class, args);
    }

}
