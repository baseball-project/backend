package com.example.baseballprediction.domain.reply.dto;

import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.reply.entity.Reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyResponse {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReplyDTO {
		private ProfileDTO profile;
		private Long replyId;
		private String content;
		private int likeCount;

		public ReplyDTO(Reply reply) {
			this.profile = new ProfileDTO(reply.getMember());
			this.replyId = reply.getId();
			this.content = reply.getContent();
			this.likeCount = 0;
		}
	}

	@Getter
	public static class ProfileDTO {
		private String profileImageUrl;
		private String nickname;
		private String teamName;
		private String teamLogoUrl;

		public ProfileDTO(Member member) {
			this.profileImageUrl = member.getProfileImageUrl();
			this.nickname = member.getNickname();
			this.teamName = member.getTeam().getName();
			this.teamLogoUrl = member.getTeam().getLogoUrl();
		}
	}
}
