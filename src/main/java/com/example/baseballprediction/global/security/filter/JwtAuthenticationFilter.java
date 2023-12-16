package com.example.baseballprediction.global.security.filter;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.global.security.auth.JwtTokenProvider;
import com.example.baseballprediction.global.security.auth.MemberDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);

        if (jwtTokenProvider == null) {
            this.jwtTokenProvider = new JwtTokenProvider();
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String jwt = request.getHeader(JwtTokenProvider.HEADER);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = jwt.replace(JwtTokenProvider.TOKEN_PREFIX, "");

        if (!jwtTokenProvider.validateToken(jwt)) {
            filterChain.doFilter(request, response);
        }

        Member member = Member.builder()
                .id(jwtTokenProvider.getMemberIdFromToken(jwt))
                .nickname(jwtTokenProvider.getNicknameFromToken(jwt))
                .build();

        MemberDetails memberDetails = new MemberDetails(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDetails, memberDetails.getPassword(), memberDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
