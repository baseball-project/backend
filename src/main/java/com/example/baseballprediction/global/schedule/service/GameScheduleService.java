package com.example.baseballprediction.global.schedule.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.baseballprediction.global.scrape.service.ScrapeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameScheduleService {
	private final ScrapeService scrapeService;

	@Scheduled(cron = "0 0 2 1 3 * ", zone = "Asia/Seoul")
	public void addGameSchedulesAll() {
		scrapeService.addSchedules();
	}

	@Scheduled(cron = "0 0 18 * * 1-5", zone = "Asia/Seoul")
	public void updateGameScoreByWeekDay() {
		scrapeService.updateGameScore();
	}

	@Scheduled(cron = "0 0 13 * * 0,6", zone = "Asia/Seoul")
	public void updateGameScoreByWeekend() {
		scrapeService.updateGameScore();
	}
}
