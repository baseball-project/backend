package com.example.baseballprediction.kbo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.kbo.dto.KboResponse.KboDtoDaily;
import com.example.baseballprediction.kbo.repository.KboRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KboService {

	private final KboRepository kboRepository;
	

	public List<KboDtoDaily> findDailyKbo(){
		List<Game> games = kboRepository.findAll();
		
		List<KboDtoDaily> kboDTOList = new ArrayList<>();
		
		for(Game game : games) {
			String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String gameFormatDate = game.getStartedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			
			  if(gameFormatDate.equals(formatDate)) {
				KboDtoDaily dailyDto = new KboDtoDaily(game,game.getAwayTeam() ,game.getHomeTeam());
					
				kboDTOList.add(dailyDto);
			  
			  }
			
		}
		
		return kboDTOList;
		
		
	}
	
	
}