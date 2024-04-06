package com.example.baseballprediction.global.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.baseballprediction.domain.chat.service.ChatService;
import com.example.baseballprediction.domain.game.entity.Game;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class ChatAspect {
	private final ChatService chatService;

	@AfterReturning("execution(* com.example.baseballprediction.domain.game..*Service.updateWinTeam(..))")
	public void removeChatRoomSession(JoinPoint joinPoint) {
		Game game = (Game)joinPoint.getArgs()[0];

		chatService.closeChatRoom(game.getId());
	}
}
