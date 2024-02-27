package com.example.baseballprediction.domain.chat.service;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class ChatService {
	
	// 채팅방 ID와 그 채팅방에 참여중인 사용자 세션 ID의 맵핑
    private Map<String, Set<String>> chatRooms = new ConcurrentHashMap<>();
    //클라이언트가 예기치 못하게게 끊겼을 경우 
    private ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();
    
    // 사용자가 채팅방에 입장할 때 호출됨
    public void addChatRoom(String sessionId, String gameId) {
        // 채팅방이 존재하지 않으면 새로 생성
        chatRooms.putIfAbsent(gameId, new HashSet<>());
        // 해당 채팅방에 클라이언트 세션 추가
        chatRooms.get(gameId).add(sessionId);
    }

    // 사용자가 채팅방에서 퇴장할 때 호출됨
    public void removeMembeSessionChatRoom(String sessionId, String gameId) {
        // 해당 채팅방에서 클라이언트 세션 제거
        chatRooms.computeIfPresent(gameId, (key, sessions) -> {
        sessions.remove(sessionId);
        return sessions;
        });
    }
    // 채팅방의 모든 사용자 세션을 종료하는 메서드
    public void closeChatRoom(String gameId) {
    	chatRooms.remove(gameId);
    }
    
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
