package com.example.baseballprediction.global.error.exception;

import com.example.baseballprediction.global.constant.ErrorCode;

public class InsufficientTokenException extends BusinessException {
	
    public InsufficientTokenException(ErrorCode errorCode) {
    	super(errorCode);
    }
	    
}
