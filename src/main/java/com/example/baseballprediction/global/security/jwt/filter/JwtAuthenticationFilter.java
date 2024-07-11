package com.example.baseballprediction.global.security.jwt.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.JwtException;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;
import com.example.baseballprediction.global.security.jwt.repository.RefreshTokenRepository;
import com.example.baseballprediction.global.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	// public JwtAuthenticationFilter() {
	// 	if (jwtTokenProvider == null) {
	// 		this.jwtTokenProvider = new JwtTokenProvider();
	// 	}
	// }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		// FilterChain filterChain) throws IOException, ServletException {
		// Cookie[] cookies = request.getCookies();
		//
		// if (cookies == null) {
		// 	filterChain.doFilter(request, response);
		// 	return;
		// }
		//
		// String refreshTokenId = null;
		// for (Cookie cookie : cookies) {
		// 	if (cookie.getName().equals("refreshToken")) {
		// 		refreshTokenId = cookie.getValue();
		// 	}
		// }
		//
		// String accessToken = request.getHeader(JwtTokenProvider.HEADER);
		//
		// if (accessToken == null) {
		// 	filterChain.doFilter(request, response);
		// 	return;
		// }
		//
		// accessToken = accessToken.replace(JwtTokenProvider.TOKEN_PREFIX, "");
		//
		// if (JwtTokenProvider.extractExpiredCheck(accessToken) && refreshTokenId != null && request.getRequestURI()
		// 	.equals("/token")) {
		// 	filterChain.doFilter(request, response);
		// 	return;
		// }
		//
		// try {
		// 	if (!JwtTokenProvider.validateToken(accessToken)) {
		//
		// 		filterChain.doFilter(request, response);
		// 	}
		//
		// 	RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenId)
		// 		.orElseThrow(() -> new JwtException(
		// 			ErrorCode.JWT_INVALID));
		//
		// 	if (refreshToken.isInvalid()) {
		// 		throw new JwtException(ErrorCode.JWT_EXPIRED);
		// 	}
		//
		// } catch (JwtException e) {
		// 	setErrorResponse(response, e);
		// 	return;
		// }
		//
		// Member member = Member.builder()
		// 	.id(JwtTokenProvider.getMemberIdFromToken(accessToken))
		// 	.username(JwtTokenProvider.getUsernameFromToken(accessToken))
		// 	.build();
		//
		// MemberDetails memberDetails = new MemberDetails(member);
		//
		// UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDetails,
		// 	memberDetails.getPassword(), memberDetails.getAuthorities());
		//
		// authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		// SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		//
		// filterChain.doFilter(request, response);

		FilterChain filterChain) throws IOException, ServletException {
		String jwt = request.getHeader(JwtTokenProvider.HEADER);

		if (jwt == null) {
			filterChain.doFilter(request, response);
			return;
		}

		jwt = jwt.replace(JwtTokenProvider.TOKEN_PREFIX, "");

		try {
			if (!JwtTokenProvider.validateToken(jwt)) {
				filterChain.doFilter(request, response);
			}
		} catch (JwtException e) {
			setErrorResponse(response, e);
			return;
		}

		Member member = Member.builder()
			.id(JwtTokenProvider.getMemberIdFromToken(jwt))
			.username(JwtTokenProvider.getUsernameFromToken(jwt))
			.build();

		MemberDetails memberDetails = new MemberDetails(member);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDetails,
			memberDetails.getPassword(), memberDetails.getAuthorities());

		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		filterChain.doFilter(request, response);
	}

	private void setErrorResponse(HttpServletResponse servletResponse, BusinessException e) throws
		IOException {
		ObjectMapper mapper = new ObjectMapper();
		ApiResponse<String> response = ApiResponse.createException(e.getCode(), e.getMessage());

		servletResponse.setStatus(e.getCode());
		servletResponse.setContentType("application/json; charset=utf-8");
		String body = mapper.writeValueAsString(response);
		servletResponse.getOutputStream().write(body.getBytes());
	}
}
