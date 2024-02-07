package com.example.baseballprediction.domain.game.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.baseballprediction.domain.game.dto.GameResponse.GameDtoDaily;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.dto.GameVoteRatioDTO;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

	private final GameRepository gameRepository;
	private final GameVoteRepository gameVoteRepository;
	

	public List<GameDtoDaily> findDailyGame(){
		List<Game> games = gameRepository.findAll();
		
		List<GameDtoDaily> gameDTOList = new ArrayList<>();
		
		for(Game game : games) {
			String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String gameFormatDate = game.getStartedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			
			  if(gameFormatDate.equals(formatDate)) {
				  GameVoteRatioDTO gameVoteRatioDTO = gameVoteRepository.findVoteRatio(game.getHomeTeam().getId(), game.getAwayTeam().getId(), game.getId()).orElseThrow();
				  
				  GameDtoDaily dailygame = new GameDtoDaily(game,game.getHomeTeam(),game.getAwayTeam(),gameVoteRatioDTO);
					
				  gameDTOList.add(dailygame);
			  
			  }
			
		}
		
		return gameDTOList;
		
		
	}
	
	
}