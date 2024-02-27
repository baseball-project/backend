package com.example.baseballprediction.global.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.baseballprediction.global.stomp.handler.StompHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {
	
	private final StompHandler stompHandler;
	
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS(); 
		// 서버 로컬에서 테스트 할 경우 주석 풀기 
		//registry.addEndpoint("/chat").setAllowedOrigins("*"); 
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메시지 브로커가 해당 api를 구독하고 있는 클라이언트에게 메시지를 전달
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub"); //메시지를 발행하기 위한 prefix
    }
    
    // jwt 토큰 인증 핸들러
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
