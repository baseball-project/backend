package com.example.baseballprediction.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.baseballprediction.domain.chat.dto.ChatRequestDTO;
import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.gifttoken.repository.GiftTokenRepository;
import com.example.baseballprediction.domain.member.dto.MemberRequest;
import com.example.baseballprediction.domain.member.dto.MemberResponse;
import com.example.baseballprediction.domain.member.dto.ProfileProjection;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.member.repository.MemberRepository;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.domain.team.repository.TeamRepository;
import com.example.baseballprediction.global.constant.ErrorCode;
import com.example.baseballprediction.global.constant.ImageType;
import com.example.baseballprediction.global.error.exception.BusinessException;
import com.example.baseballprediction.global.error.exception.NotFoundException;
import com.example.baseballprediction.global.util.S3UploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final S3UploadService s3UploadService;
	private final GiftTokenRepository giftTokenRepository;

	@Transactional
	public void modifyLikeTeam(String username, int teamId) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
		Team likeTeam = teamRepository.findById(teamId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

		member.changeTeam(likeTeam);

		member.setIsNewMember(false);
	}

	@Transactional
	public void modifyDetails(String username, MemberRequest.DetailsDTO detailsDTO, MultipartFile profileImage) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		if (isExistNickname(member.getNickname(), detailsDTO.getNickname())) {
			throw new BusinessException(ErrorCode.MEMBER_NICKNAME_EXIST);
		}

		String uploadFileName = null;
		if (profileImage != null) {
			uploadFileName = s3UploadService.updateFile(profileImage, member.getProfileImageUrl(),
				ImageType.PROFILE);
		}

		member.updateDetails(uploadFileName, detailsDTO.getNickname(), detailsDTO.getComment());

		if (member.isNewMember()) {
			member.setIsNewMember(false);
		}
	}

	@Transactional(readOnly = true)
	public MemberResponse.NicknameDTO findExistNickname(Long memberId, String nickname) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		boolean isExist = isExistNickname(member.getNickname(), nickname);

		return new MemberResponse.NicknameDTO(isExist);
	}

	private boolean isExistNickname(String originNickname, String nickname) {

		if (originNickname.equals(nickname))
			return false;

		return memberRepository.findByNickname(nickname).isPresent();
	}

	public ProfileProjection findProfile(String nickname) {
		ProfileProjection profile = memberRepository.findProfile(nickname)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		return profile;
	}

	public MemberResponse.ProfileDTO findDetails(String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		MemberResponse.ProfileDTO details = new MemberResponse.ProfileDTO(member);

		return details;
	}

	@Transactional
	public void saveGiftToken(Long senderId, ChatRequestDTO.ChatGiftRequestDTO chatGiftRequestDTO) {
		Member sender = memberRepository.findById(senderId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		Member recipient = memberRepository.findByNickname(chatGiftRequestDTO.getRecipientNickName())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		if (sender.getNickname().equals(recipient.getNickname())) {
			throw new BusinessException(ErrorCode.GIFTING_TO_SELF_NOT_ALLOWED);
		}
		int senderCurrentToken = sender.getToken();

		int token = chatGiftRequestDTO.getToken();

		if (senderCurrentToken < token) {
			throw new BusinessException(ErrorCode.INSUFFICIENT_TOKENS);
		}
		sender.addToken(-token);
		recipient.addToken(token);

		GiftToken giftToken = GiftToken.builder()
			.takeMember(sender)
			.giveMember(recipient)
			.tokenAmount(token)
			.comment(chatGiftRequestDTO.getComment())
			.build();
		giftTokenRepository.save(giftToken);
	}
}
