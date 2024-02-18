package com.example.baseballprediction.global.security.jwt;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.error.exception.JwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

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
		} catch (ExpiredJwtException e) {
			throw new JwtException(ErrorCode.JWT_EXPIRED);
		} catch (MalformedJwtException | IllegalArgumentException | UnsupportedJwtException | SignatureException |
				 BadCredentialsException | AuthenticationCredentialsNotFoundException e) {
			throw new JwtException(ErrorCode.JWT_INVALID);
		} catch (Exception e) {
			throw new JwtException(ErrorCode.INTERNAL_SERVER_ERROR);
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

	public static String createToken(String username) {
		Claims claims = Jwts.claims();
		claims.put("username", username);

		String jwt = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + EXP))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();

		return TOKEN_PREFIX + jwt;
	}

	public static void expireToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		claims.setExpiration(new Date(0));
		Jwts.builder()
			.setClaims(claims)
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}
}
