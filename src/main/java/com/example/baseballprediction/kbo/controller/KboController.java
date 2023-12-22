package com.example.baseballprediction.kbo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.baseballprediction.global.util.ApiResponse;
import com.example.baseballprediction.kbo.dto.KboResponse.KboDtoDaily;
import com.example.baseballprediction.kbo.service.KboService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class KboController {
	
	private final KboService kboService;
	
	@GetMapping("")
	public ResponseEntity<ApiResponse<List<KboDtoDaily>>> kboDailyTodayList() {
		
		List<KboDtoDaily> kboDtoDailyList = kboService.findDailyKbo();
		
		ApiResponse<List<KboDtoDaily>> response = ApiResponse.success(kboDtoDailyList);
		
		return ResponseEntity.ok(response);
		
	}

}
