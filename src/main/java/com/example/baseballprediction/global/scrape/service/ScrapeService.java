package com.example.baseballprediction.global.scrape.service;

import java.time.LocalDateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.Status;
import com.example.baseballprediction.global.util.CustomDateUtil;
import com.example.baseballprediction.global.util.WebDriverUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapeService {
	private final TeamRepository teamRepository;
	private final GameRepository gameRepository;

	private String currentDate;

	public void addSchedules() {
		WebDriver webDriver = WebDriverUtil.getChromeDriver();
		try {
			webDriver.get("https://www.koreabaseball.com/Schedule/Schedule.aspx");
			Select yearSelector = new Select(webDriver.findElement(By.id("ddlYear")));
			yearSelector.selectByIndex(0);
			String year = yearSelector.getFirstSelectedOption().getAttribute("value");

			Select monthSelector = new Select(webDriver.findElement(By.id("ddlMonth")));
			Select seriesSelector = new Select(webDriver.findElement(By.id("ddlSeries")));

			for (int i = 0; i < 2; i++) {
				seriesSelector.selectByIndex(i);

				for (int j = 2; j < 12; j++) {
					if (i == 0 && j > 2) {
						break;
					}
					monthSelector.selectByIndex(j);

					Document doc = Jsoup.parse(webDriver.getPageSource());
					Elements baseballSchedules = doc.select("#tblScheduleList > tbody > tr");

					this.currentDate = null;

					for (Element schedule : baseballSchedules) {
						addGameScheduleByDay(schedule, year);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webDriver.quit();
		}
	}

	public void updateGameScore() {
		WebDriver webDriver = WebDriverUtil.getChromeDriver();
		try {
			webDriver.get("https://www.koreabaseball.com/Schedule/GameCenter/Main.aspx");
			Document doc = Jsoup.parse(webDriver.getPageSource());
			String gameDate = doc.select("ul[class=date]")
				.select("li[class=today]")
				.text()
				.replace(".", "-")
				.substring(0, 10);

			Elements gameElements = doc.select("ul[class=game-list-n]")
				.select("li[g_dt=" + gameDate.replace("-", "") + "]");

			for (Element gameElement : gameElements) {
				try {
					String status = gameElement.className().split(" ")[1];
					String gameTime = gameElement.select("div[class=top]").select("ul > li:nth-child(3)").text();
					Team homeTeam = teamRepository.findByShortName(gameElement.attributes().get("home_nm"))
						.orElseThrow();
					Team awayTeam = teamRepository.findByShortName(gameElement.attributes().get("away_nm"))
						.orElseThrow();
					Elements teamElements = gameElement.select("div[class=middle]").select("div[class=info]");
					String homeScoreStr = teamElements.select("div[class=team home]").select("div:nth-child(2)").text();
					String awayScoreStr = teamElements.select("div[class=team away]").select("div:nth-child(2)").text();
					int homeScore = Integer.parseInt(
						homeScoreStr.isEmpty() ? "0" : homeScoreStr);
					int awayScore = Integer.parseInt(
						awayScoreStr.isEmpty() ? "0" : awayScoreStr);

					LocalDateTime startedAt = CustomDateUtil.toDateTime(gameDate + " " + gameTime);

					Game game = gameRepository.findByHomeTeamAndAwayTeamAndStartedAt(homeTeam, awayTeam, startedAt)
						.orElseThrow();

					Status newStatus = Status.findByName(status);

					game.updateByScrapeData(homeScore, awayScore, newStatus);

					if (!Status.END.getName().equals(status)) {
						continue;
					}

					if (homeScore > awayScore) {
						game.updateWinTeam(homeTeam);
					} else {
						game.updateWinTeam(awayTeam);
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webDriver.quit();
		}
	}

	@Transactional(readOnly = true)
	private void addGameScheduleByDay(Element schedule, String year) {
		Element date = schedule.selectFirst("td.day");
		Element time = schedule.selectFirst("td.time");
		Element awayTeamElement = schedule.selectFirst("td.play > span");
		Element homeTeamElement = schedule.selectFirst("td.play > span:nth-child(3)");

		if (date != null && (currentDate == null || !currentDate.equals(date.text()))) {
			currentDate = date.text();
		}

		if (time != null) {
			LocalDateTime startedAt = CustomDateUtil.toDateTime(
				year + "-" + currentDate.substring(0, 5).replace(".", "-") + " " + time.text());
			Team homeTeam = teamRepository.findByShortName(homeTeamElement.text()).orElseThrow();
			Team awayTeam = teamRepository.findByShortName(awayTeamElement.text()).orElseThrow();

			addGame(homeTeam, awayTeam, startedAt);
		}
	}

	private void addGame(Team homeTeam, Team awayTeam, LocalDateTime startedAt) {
		Game game = Game.builder()
			.homeTeam(homeTeam)
			.awayTeam(awayTeam)
			.startedAt(startedAt)
			.status(Status.READY)
			.homeTeamScore(0)
			.awayTeamScore(0)
			.build();

		gameRepository.save(game);
	}
}
