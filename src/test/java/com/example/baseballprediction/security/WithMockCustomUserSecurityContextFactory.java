package com.example.baseballprediction.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.baseballprediction.annotation.WithTestUser;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.global.security.MemberDetails;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithTestUser> {
	@Override
	public SecurityContext createSecurityContext(WithTestUser testUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
		Member member = Member.builder()
			.username(testUser.username())
			.id(1L)
			.build();

		MemberDetails memberDetails = new MemberDetails(member);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(memberDetails,
			memberDetails.getPassword(), memberDetails.getAuthorities());

		authentication.setDetails(memberDetails);
		context.setAuthentication(authentication);
		return context;
	}
}
