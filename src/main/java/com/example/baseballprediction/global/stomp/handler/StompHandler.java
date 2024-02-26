package com.example.baseballprediction.global.stomp.handler;


import com.example.baseballprediction.domain.chat.service.ChatService;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.game.repository.GameRepository;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
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
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String gameId = accessor.getFirstNativeHeader("gameId");
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
            if (gameId != null && authorizationHeader != null) {
                String extractedToken = extractToken(authorizationHeader);
                if (extractedToken != null && jwtTokenProvider.validateToken(extractedToken)) {
                	 Long memberId = JwtTokenProvider.getMemberIdFromToken(extractedToken);
                     Optional<Game> game = gameRepository.findById(Long.parseLong(gameId));
                    if (game.isPresent() && canEnterChatRoom(game.get())) {
                        MemberDetails memberDetails = createMemberDetails(memberId);

                        // 세션 속성이 null인 경우 빈 Map을 생성하고 설정
                        if (accessor.getSessionAttributes() == null) {
                            accessor.setSessionAttributes(new ConcurrentHashMap<>());
                        }
                        String sessionId = accessor.getSessionId();
                        chatService.addChatRoom(sessionId, gameId);
                        accessor.getSessionAttributes().put("memberDetails", memberDetails);
                    } else {
                        // 채팅방에 입장할 수 없는 경우 처리
                    	String errorMessagePayload = "채팅방에 입장할 수 없습니다.";
    	                MessageHeaders headers = accessor.getMessageHeaders();
    	                MessageBuilder.createMessage(errorMessagePayload, headers);
    	                return null; 
                    }
                }
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            // 나가기 버튼이 아닌 채팅방 강제로 닫거나 인터넷 연결이 끊어질 경우 
        	String sessionId = accessor.getSessionId();
        	chatService.removeSession(sessionId);  
        }

        return message;
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private boolean canEnterChatRoom(Game game) {
        return game.getStatus().equals(Status.READY) || game.getStatus().equals(Status.PROGRESS);
    }

    private MemberDetails createMemberDetails(Long memberId) {
        // MemberService를 사용하여 memberId에 해당하는 Member를 조회
    	 Member member = memberRepository.findById(memberId)
                 .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND)); // 예시 메소드, 실제로는 MemberService에 정의된 메소드를 호출해야 합니다.

        // 조회한 회원 정보를 사용하여 MemberDetails 객체를 생성하여 반환
        return new MemberDetails(member);
    }
}

