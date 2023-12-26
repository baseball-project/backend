package com.example.baseballprediction.domain.game.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.baseballprediction.domain.game.dto.GameResponse.GameDtoDaily;
import com.example.baseballprediction.domain.game.service.GameService;
import com.example.baseballprediction.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {
	
	private final GameService gameService;
	
	@GetMapping("")
	public ResponseEntity<ApiResponse<List<GameDtoDaily>>> gameDailyTodayList() {
		
		List<GameDtoDaily> gameDtoDailyList = gameService.findDailyGame();
		
		ApiResponse<List<GameDtoDaily>> response = ApiResponse.success(gameDtoDailyList);
		
		return ResponseEntity.ok(response);
		
	}

}
