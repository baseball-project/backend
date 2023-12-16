package com.example.baseballprediction.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberRequest {
    @Getter
    @AllArgsConstructor
    public static class LoginDTO {
        private String username;
        private String password;
    }
}
