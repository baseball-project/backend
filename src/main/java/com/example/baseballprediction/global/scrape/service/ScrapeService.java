package com.example.baseballprediction.global.scrape.service;

import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.Status;
import com.example.baseballprediction.global.util.CustomDateUtil;
import com.example.baseballprediction.global.util.WebDriverUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapeService {
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;

    public void addSchedules() {
        WebDriver webDriver = WebDriverUtil.getChromeDriver();
        try {
            webDriver.get("https://www.koreabaseball.com/Schedule/Schedule.aspx");
            Select yearSelector = new Select(webDriver.findElement(By.id("ddlYear")));
            yearSelector.selectByIndex(0);
            String year = yearSelector.getFirstSelectedOption().getAttribute("value");

            Select monthSelector = new Select(webDriver.findElement(By.id("ddlMonth")));
            monthSelector.selectByValue("04");

            Document doc = Jsoup.parse(webDriver.getPageSource());
            Elements baseballSchedules = doc.select("#tblScheduleList > tbody > tr");

            String currentDate = null;
            for (Element schedule : baseballSchedules) {
                Element date = schedule.selectFirst("td.day");
                Element time = schedule.selectFirst("td.time");
                Element awayTeamElement = schedule.selectFirst("td.play > span");
                Element homeTeamElement = schedule.selectFirst("td.play > span:nth-child(3)");

                if (date != null && (currentDate == null || !currentDate.equals(date.text()))) {
                    currentDate = date.text();
                }

                if (time != null) {
                    LocalDateTime startedAt = CustomDateUtil.toDateTime(year + "-" + currentDate.substring(0, 5).replace(".", "-") + " " + time.text());
                    Team homeTeam = teamRepository.findByShortName(homeTeamElement.text()).orElseThrow();
                    Team awayTeam = teamRepository.findByShortName(awayTeamElement.text()).orElseThrow();

                    Game game = Game.builder()
                            .homeTeam(homeTeam)
                            .awayTeam(awayTeam)
                            .startedAt(startedAt.plusMonths(8).plusDays(14))
                            .status(Status.READY)
                            .homeTeamScore(0)
                            .awayTeamScore(0)
                            .build();
                    
                    gameRepository.save(game);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
    }
}
