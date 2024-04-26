package com.example.baseballprediction.domain.chat.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class ChatRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatGiftRequestDTO {
        
        @NotBlank(message = "선물 받는 사람의 닉네임은 필수입니다.")
        private String recipientNickName;
        
        @Min(value = 1, message = "토큰은 최소 1개 이상이어야 합니다.")
        private int token;
        
        @Size(max = 100, message = "한마디는 100자 이하로 입력해주세요.")
        private String comment;
    }

    @Getter
    public static class ChatLeaveRequest {
        @NotNull(message = "gameId는 필수입니다.")
        private Long gameId;
    }
}