package com.example.baseballprediction.global.scrape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.global.scrape.service.ScrapeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/scrape")
@RequiredArgsConstructor
public class ScrapeController {
	private final ScrapeService scrapeService;

	@PostMapping("/schedules")
	public ResponseEntity<?> scrapeSchedules() {
		scrapeService.addSchedules();

		return ResponseEntity.ok().build();
	}

	@PostMapping("/scores")
	public ResponseEntity<?> scrapeScores() {
		scrapeService.updateGameScore();

		return ResponseEntity.ok().build();
	}
}
