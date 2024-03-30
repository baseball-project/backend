package com.example.baseballprediction.global.security.oauth.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.security.jwt.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		// ObjectMapper objectMapper = new ObjectMapper();
		MemberDetails memberDetails = (MemberDetails)authentication.getPrincipal();
		String token = JwtTokenProvider.createToken(memberDetails.getMember());

		// response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		// response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		//
		// response.addHeader(JwtTokenProvider.HEADER, token);

		// boolean isNewMember = memberDetails.isNewMember();
		// String teamName =
		// 	memberDetails.getMember().getTeam() == null ? null : memberDetails.getMember().getTeam().getName();
		// LoginDTO loginDTO = new LoginDTO(isNewMember, memberDetails.getProfileImageUrl(), memberDetails.getName(),
		// 	teamName);

		String targetUrl = UriComponentsBuilder.fromUriString(request.getRequestURI())
			.queryParam("accessToken", token)
			.build()
			.encode(StandardCharsets.UTF_8)
			.toUriString();

		response.sendRedirect(targetUrl);
	}
}
