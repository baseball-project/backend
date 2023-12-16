package com.example.baseballprediction.domain.member.service;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.security.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, Object> login(String username, String password) {
        Member member = memberRepository.findByUsername(username).orElseThrow();

        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("패스워드가 일치하지 않습니다.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtTokenProvider.createToken(member));

        return response;
    }
}
