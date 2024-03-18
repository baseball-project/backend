package com.example.baseballprediction.domain.member.dto;

import com.example.baseballprediction.domain.gifttoken.entity.GiftToken;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import com.example.baseballprediction.global.util.CustomDateUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {
	@Getter
	public static class ProfileDTO {
		private String nickname;
		private String profileImageUrl;
		private int level;
		private int token;
		private String teamName;
		private String comment;

		public ProfileDTO(Member member) {
			this.nickname = member.getNickname();
			this.profileImageUrl = member.getProfileImageUrl();
			this.level = member.getLevel();
			this.token = member.getToken();
			this.teamName = member.getTeam().getName();
			this.comment = member.getComment();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FairyHistoryDTO {
		private String type;
		private int rank;
		private String comment;
		private String historyDate;

		public FairyHistoryDTO(Member member, MonthlyFairy monthlyFairy) {
			this.type = monthlyFairy.getType().getName();
			this.rank = monthlyFairy.getRank();
			this.comment = member.getComment();
			this.historyDate = CustomDateUtil.dateToString(monthlyFairy.getCreatedAt());
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GiftHistoryDTO {
		private String nickname;
		private String teamName;
		private int token;
		private String takeDate;

		public GiftHistoryDTO(GiftToken giftToken) {
			this.nickname = giftToken.getTakeMember().getNickname();
			this.teamName = giftToken.getTakeMember().getTeam().getName();
			this.token = giftToken.getTokenAmount();
			this.takeDate = CustomDateUtil.dateToString(giftToken.getCreatedAt());
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NicknameDTO {
		private boolean exist;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LoginDTO {
		private String profileImageUrl;
		private String nickname;
		private String teamName;

		public LoginDTO(Member member) {
			this.profileImageUrl = member.getProfileImageUrl();
			this.nickname = member.getNickname();
			this.teamName = member.getTeam().getName();
		}
	}
}
