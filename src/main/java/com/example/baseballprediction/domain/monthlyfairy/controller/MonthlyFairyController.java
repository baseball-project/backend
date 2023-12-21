package com.example.baseballprediction.domain.monthlyfairy.controller;

import static com.example.baseballprediction.domain.monthlyfairy.dto.MonthlyFairyResponse.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.monthlyfairy.service.MonthlyFairyService;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class MonthlyFairyController {
	private final MonthlyFairyService monthlyFairyService;

	@GetMapping("")
	public ResponseEntity<ApiResponse<StatisticsDTO>> monthlyFairyList() {
		StatisticsDTO statistics = monthlyFairyService.findStatistics();

		ApiResponse<StatisticsDTO> response = ApiResponse.success(statistics);

		return ResponseEntity.ok(response);
	}
}
