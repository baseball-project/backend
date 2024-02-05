package com.example.baseballprediction.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.baseballprediction.global.security.jwt.filter.JwtAuthenticationFilter;
import com.example.baseballprediction.global.security.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.example.baseballprediction.global.security.oauth.service.OAuth2MemberService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2MemberService oAuth2MemberService;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authenticationConfiguration
	) throws Exception {

		return authenticationConfiguration.getAuthenticationManager();
	}

	public class CustomSecurityFilterManager extends
		AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {

		@Override
		public void configure(HttpSecurity builder) throws Exception {

			AuthenticationManager authenticationManager = builder.getSharedObject(
				AuthenticationManager.class);
			builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
			super.configure(builder);
		}
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable);
		httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
		httpSecurity.headers(c1 -> c1.frameOptions(c2 -> c2.sameOrigin()));
		httpSecurity.cors(c -> c.configurationSource(configurationSource()));
		httpSecurity.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		httpSecurity.formLogin(AbstractHttpConfigurer::disable);

		httpSecurity.apply(new CustomSecurityFilterManager());

		httpSecurity.authorizeHttpRequests((request) -> request
			.requestMatchers(new AntPathRequestMatcher("/login"),
				new AntPathRequestMatcher("/h2-console/**"),
				new AntPathRequestMatcher("/health"),
				new AntPathRequestMatcher("/games")
					).permitAll()
			.anyRequest().authenticated());

		httpSecurity.oauth2Login(oauth2configurer -> {
				oauth2configurer.userInfoEndpoint(user -> user.userService(oAuth2MemberService));
				oauth2configurer.successHandler(oAuth2AuthenticationSuccessHandler);
			}
		);

		return httpSecurity.build();
	}

	@Bean
	public CorsConfigurationSource configurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
		configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트 앤드 IP만 허용 react)
		configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
		configuration.addExposedHeader("Authorization"); // 옛날에는 디폴트 였다. 지금은 아닙니다.
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
