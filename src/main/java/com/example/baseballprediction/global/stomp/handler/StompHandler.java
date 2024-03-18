package com.example.baseballprediction.global.stomp.handler;


import com.example.baseballprediction.domain.chat.service.ChatService;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
import com.example.baseballprediction.domain.gamevote.repository.GameVoteRepository;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.Status;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {
	
	private final JwtTokenProvider jwtTokenProvider;
    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;
    private final GameVoteRepository gameVoteRepository;
	
	@Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                String sessionId = accessor.getSessionId();
                chatService.removeSession(sessionId); 
                return null; 
            }
            return message; 
        }

        String gameId = accessor.getFirstNativeHeader("gameId");
        String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
        
        // gameId 또는 Authorization 헤더가 누락된 경우
        if (gameId == null || authorizationHeader == null) {
        	 return createErrorMessage(accessor.getSessionId(), "gameId 및 인증 토큰 값이 없습니다.");
        }

        String extractedToken = extractToken(authorizationHeader);
        // 토큰이 유효하지 않은 경우
        if (extractedToken == null || !jwtTokenProvider.validateToken(extractedToken)) {
        	 return createErrorMessage(accessor.getSessionId(), "토큰이 유효하지 않습니다.");
        }

        Long memberId = JwtTokenProvider.getMemberIdFromToken(extractedToken);
        Optional<Game> game = gameRepository.findById(Long.parseLong(gameId));

        // 게임이 존재하지 않거나 채팅방에 입장할 수 없는 경우 처리
        if (!game.isPresent() || !canEnterChatRoom(game.get())) {
        	 return createErrorMessage(accessor.getSessionId(), "게임이 존재하지 않거나 채팅방에 입장 할 수 없습니다.");
            
        }

		boolean gameVoteExists = gameVoteRepository.existsByGameIdAndMemberId(Long.parseLong(gameId), memberId);
		
		if (!gameVoteExists) {
		    return createErrorMessage(accessor.getSessionId(), "투표 완료 후 채팅방에 입장 할 수 있습니다.");
		}
        
        MemberDetails memberDetails = createMemberDetails(memberId);
        if (accessor.getSessionAttributes() == null) {
            accessor.setSessionAttributes(new ConcurrentHashMap<>());
        }
        String sessionId = accessor.getSessionId();
        chatService.addChatRoom(sessionId, gameId);
        accessor.getSessionAttributes().put("memberDetails", memberDetails);

        return message;
        
	}
	
	private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private boolean canEnterChatRoom(Game game) {
        return game.getStatus().equals(Status.PROGRESS);
    }

    private MemberDetails createMemberDetails(Long memberId) {
        // MemberService를 사용하여 memberId에 해당하는 Member를 조회
    	 Member member = memberRepository.findById(memberId)
                 .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND)); 

        // 조회한 회원 정보를 사용하여 MemberDetails 객체를 생성하여 반환
        return new MemberDetails(member);
    }
    
    private Message<?> createErrorMessage(String sessionId, String errorMessage) {
        byte[] errorPayload = errorMessage.getBytes(StandardCharsets.UTF_8);
        
        // 에러 메시지 전송을 위한 SimpMessageHeaderAccessor 설정
        SimpMessageHeaderAccessor errorAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        errorAccessor.setSessionId(sessionId);
        errorAccessor.setLeaveMutable(true);
        
        // 클라이언트에게 에러 메시지 전송
        return MessageBuilder.createMessage(errorPayload, errorAccessor.getMessageHeaders());
    }
	

	
}