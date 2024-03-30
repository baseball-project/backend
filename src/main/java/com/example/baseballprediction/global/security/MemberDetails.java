package com.example.baseballprediction.global.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.baseballprediction.domain.member.entity.Member;

public class MemberDetails implements UserDetails {
	private final Member member;

	private Map<String, Object> attributes;

	public MemberDetails(Member member) {
		this.member = member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getUsername();
	}

	public String getName() {
		return member.getNickname();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public boolean isNewMember() {
		return member.isNewMember();
	}

	public String getProfileImageUrl() {
		return member.getProfileImageUrl();
	}

	public Integer getTeamId() {
		return member.getTeam().getId();
	}

	public Member getMember() {
		return this.member;
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}
}
