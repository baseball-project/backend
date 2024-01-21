package com.example.baseballprediction.global.security.oauth.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.global.constant.SocialType;
import com.example.baseballprediction.global.security.MemberDetails;
import com.example.baseballprediction.global.security.oauth.memberinfo.GoogleMemberInfo;
import com.example.baseballprediction.global.security.oauth.memberinfo.KakaoMemberInfo;
import com.example.baseballprediction.global.security.oauth.memberinfo.NaverMemberInfo;
import com.example.baseballprediction.global.security.oauth.memberinfo.OAuth2MemberInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {
	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		OAuth2MemberInfo memberInfo = getMemberInfo(oAuth2User, registrationId);

		SocialType socialType = memberInfo.getSocialType();
		String providerId = memberInfo.getProviderId();
		String nickname = socialType + "_" + providerId;  //임시 닉네임으로 사용
		String email = memberInfo.getEmail();

		Member member = createOrFindMember(email, socialType, nickname);

		return new MemberDetails(member);
	}

	private OAuth2MemberInfo getMemberInfo(OAuth2User oAuth2User, String registrationId) {
		OAuth2MemberInfo memberInfo = null;
		if (registrationId.equals("kakao")) {
			memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());
		} else if (registrationId.equals("naver")) {
			memberInfo = new NaverMemberInfo(oAuth2User.getAttributes());
		} else if (registrationId.equals("google")) {
			memberInfo = new GoogleMemberInfo(oAuth2User.getAttributes());
		}

		return memberInfo;
	}

	@Transactional
	private Member createOrFindMember(String username, SocialType socialType, String nickname) {
		Optional<Member> findMember = memberRepository.findByUsername(username);

		if (findMember.isEmpty()) {
			return createMember(username, socialType, nickname);
		}

		Member member = findMember.get();
		member.setIsNewMember(false);

		return member;
	}

	@Transactional
	private Member createMember(String username, SocialType socialType, String nickname) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String password = bCryptPasswordEncoder.encode("password");
		Member member = Member.builder()
			.username(username)
			.socialType(socialType)
			.nickname(nickname)
			.password(password)
			.isNewMember(true)
			.build();

		memberRepository.save(member);

		return member;
	}
}
