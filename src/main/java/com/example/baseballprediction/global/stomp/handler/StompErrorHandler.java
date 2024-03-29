package com.example.baseballprediction.global.stomp.handler;

import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import com.example.baseballprediction.global.constant.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class StompErrorHandler extends StompSubProtocolErrorHandler{

	@Override
	public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
	    ErrorCode errorCode = mapExceptionToErrorCode(ex.getMessage());
	    if (errorCode != null) {
	        return errorMessage(errorCode);
	    }
	    return super.handleClientMessageProcessingError(clientMessage, ex);
	}
	
	private ErrorCode mapExceptionToErrorCode(String exceptionMessage) {
	    switch (exceptionMessage) {
	        case "GAMEID_OR_AUTH_TOKEN_MISSING":
	            return ErrorCode.GAMEID_OR_AUTH_TOKEN_MISSING;
	        case "INVALID_TOKEN":
	            return ErrorCode.INVALID_TOKEN;
	        case "GAME_OR_CHATROOM_NOT_FOUND":
	            return ErrorCode.GAME_OR_CHATROOM_NOT_FOUND;
	        case "VOTING_REQUIRED_FOR_ENTRY":
	            return ErrorCode.VOTING_REQUIRED_FOR_ENTRY;
	        default:
	            return null;
	    }
	}
	
	private Message<byte[]> errorMessage(ErrorCode errorCode) {
	    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
	    accessor.setLeaveMutable(true);
	    byte[] errorPayload = errorCode.getMessage().getBytes(StandardCharsets.UTF_8);
	    return MessageBuilder.createMessage(errorPayload, accessor.getMessageHeaders());
	}
}

