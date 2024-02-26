package com.example.baseballprediction.domain.chat.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.InsufficientTokenException;
import com.example.baseballprediction.global.error.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

	private final MemberRepository memberRepository;
	// 채팅방 ID와 그 채팅방에 참여중인 사용자 세션 ID의 맵핑
    private Map<String, Set<String>> chatRooms = new ConcurrentHashMap<>();
    //클라이언트가 예기치 못하게게 끊겼을 경우 
    private ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();
	
    
    public void saveGiftToken(Long senderId, Long recipientId, int token) {
        Member sender = memberRepository.findById(senderId)
        		.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        
        Member recipient = memberRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if(sender != recipient) {
	        // 토큰 선물 로직 추가
	        int senderCurrentToken = sender.getToken();
	        int recipientCurrentToken = recipient.getToken();
	        
	        if (senderCurrentToken >= token) {
	            sender.setToken(senderCurrentToken - token);
	            recipient.setToken(recipientCurrentToken + token);
	
	            memberRepository.save(sender);
	            memberRepository.save(recipient);
	        } else {
	        	int shortage = token - senderCurrentToken;
        	    throw new InsufficientTokenException(shortage,ErrorCode.INSUFFICIENT_TOKENS);
	        }
        }else {
        	throw new InsufficientTokenException(ErrorCode.GIFTING_TO_SELF_NOT_ALLOWED);
        }
    }

    // 사용자가 채팅방에 입장할 때 호출됨
    public void addChatRoom(String sessionId, String gameId) {
        // 채팅방이 존재하지 않으면 새로 생성
        chatRooms.putIfAbsent(gameId, new HashSet<>());
        // 해당 채팅방에 클라이언트 세션 추가
        chatRooms.get(gameId).add(sessionId);
    }

    // 사용자가 채팅방에서 퇴장할 때 호출됨
    public void removeChatRoom(String sessionId, String gameId) {
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
