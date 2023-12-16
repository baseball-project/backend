package com.example.baseballprediction.domain.member.controller;

import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.service.MemberService;
import com.example.baseballprediction.global.security.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberRequest.LoginDTO loginDTO) {
        Map<String, Object> response = memberService.login(loginDTO.getUsername(), loginDTO.getPassword());

        return ResponseEntity
                .ok()
                .header(JwtTokenProvider.HEADER, (String) response.get("token"))
                .body("정상적으로 처리되었습니다.");
    }
}
