package com.example.baseballprediction.global.security.auth;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.baseballprediction.domain.member.entity.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
	//TODO : 개발 기간 동안 토큰 만료기한 7일로 설정
	public static final Long EXP = 1000L * 60 * 60 * 24 * 7;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER = "Authorization";
	private static String secretKey;

	@Value("${my-env.jwt.key}")
	public void setSecretKey(String secret) {
		secretKey = Base64.getEncoder().encodeToString(secret.getBytes());
	}

	public static Long getMemberIdFromToken(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("id", Long.class);
	}

	public static String getUsernameFromToken(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("username", String.class);
	}

	public static boolean validateToken(String token) {
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

	public static String createToken(Member member) {
		Claims claims = Jwts.claims();
		claims.put("id", member.getId());
		claims.put("username", member.getUsername());

		String jwt = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + EXP))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();

		return TOKEN_PREFIX + jwt;
	}
}
