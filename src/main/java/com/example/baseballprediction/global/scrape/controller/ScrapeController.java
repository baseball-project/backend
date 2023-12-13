package com.example.baseballprediction.global.scrape.controller;

import com.example.baseballprediction.global.scrape.service.ScrapeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
