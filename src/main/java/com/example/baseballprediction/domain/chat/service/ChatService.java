package com.example.baseballprediction.domain.chat.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.baseballprediction.domain.chat.dto.ChatProfileDTO;
import com.example.baseballprediction.domain.chat.dto.ChatRoom;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
	
	private final GameRepository gameRepository;
	private final MemberRepository memberRepository;
	private Map<String, ChatRoom> chatRoomMap = new HashMap<>();
	
	public ChatRoom addChatRoom(Long gameId){
		Optional<Game> games =  gameRepository.findById(gameId);
		String home = games.get().getAwayTeam().getName();
		String away = games.get().getHomeTeam().getName();
		ChatRoom chatRoom = new ChatRoom().create (gameId,home, away);
		chatRoomMap.put(chatRoom.getGameId().toString(), chatRoom);
		return chatRoom;
	}
	
	@Transactional(readOnly = true)
	public List<ChatRoom> findAllRoom(){
		List<ChatRoom> chatRooms = new ArrayList<>(chatRoomMap.values());
		return chatRooms;
	}
	
	@Transactional(readOnly = true)
	public Optional<ChatProfileDTO> findByUsername(Long id) {
		Optional<ChatProfileDTO> test= memberRepository.findByChatProfile(id);
		return test;
	}
}
