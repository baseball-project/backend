package com.example.baseballprediction.domain.game.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.baseballprediction.domain.game.dto.GameResponse.GameDtoDaily;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

	private final GameRepository gameRepository;
	

	public List<GameDtoDaily> findDailyGame(){
		List<Game> games = gameRepository.findAll();
		
		List<GameDtoDaily> gameDTOList = new ArrayList<>();
		
		for(Game game : games) {
			String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String gameFormatDate = game.getStartedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			
			  if(gameFormatDate.equals(formatDate)) {
				  GameDtoDaily dailygame = new GameDtoDaily(game,game.getAwayTeam() ,game.getHomeTeam());
					
				  gameDTOList.add(dailygame);
			  
			  }
			
		}
		
		return gameDTOList;
		
		
	}
	
	
}