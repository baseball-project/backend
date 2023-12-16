package com.example.baseballprediction.global.security.auth;

import com.example.baseballprediction.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    public static final Long EXP = 1000L * 60 * 60 * 24;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    @Value("${my-env.jwt.key}")
    private String secretKey;

    public Long getMemberIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
    }

    public String getNicknameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("nickname", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String createToken(Member member) {
        Claims claims = Jwts.claims();
        claims.put("id", member.getId());
        claims.put("nickname", member.getNickname());

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TOKEN_PREFIX + jwt;
    }
}
