package com.example.baseballprediction.global.error.exception;


import com.example.baseballprediction.global.constant.ErrorCode;

public class InsufficientTokenException extends RuntimeException {

	private final Integer shortage; // 부족한 토큰의 수
    private final ErrorCode errorCode; // 에러 코드
    
    // 자기 자신에게 선물할 수 없는 상황을 위한 생성자
    public InsufficientTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.shortage = null; // 이 경우에는 부족한 토큰의 수가 관련 없음
    }


    // 부족한 토큰의 수와 에러 코드를 인자로 받는 생성자
    public InsufficientTokenException(int shortage, ErrorCode errorCode) {
        super(errorCode.getMessage()  + " 부족한 토큰 수 : " + shortage);
        this.shortage = shortage;
        this.errorCode = errorCode;
    }
    

    // 부족한 토큰의 수에 대한 getter
    public int getShortage() {
        return shortage;
    }

    // 에러 코드에 대한 getter
    public ErrorCode getErrorCode() {
        return errorCode;
    }

}