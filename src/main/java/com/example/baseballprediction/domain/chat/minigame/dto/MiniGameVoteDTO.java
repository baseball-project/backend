package com.example.baseballprediction.domain.chat.minigame.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import com.example.baseballprediction.domain.chat.dto.ChatEventDTO.ChatProfileDTO;
import com.example.baseballprediction.global.constant.ChatMessageType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MiniGameVoteDTO {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Options {
		private String question;
		private String option1;
		private String option2;
	}

	@Getter
	public static class VoteMessage {
		private Long miniGameId;
		private String message;
		private ChatProfileDTO profile;
		private Options miniGames;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		@LastModifiedDate
		private LocalDateTime startedAt;

		public VoteMessage(Long miniGameId, ChatMessageType message, ChatProfileDTO profile, Options options,
			LocalDateTime startedAt) {
			this.miniGameId = miniGameId;
			this.message = message.getMessage();
			this.profile = new ChatProfileDTO(profile.getNickname(), profile.getProfileImageUrl(),
				profile.getTeamName());
			this.miniGames = new Options(options.getQuestion(), options.getOption1(), options.getOption2());
			this.startedAt = startedAt;

		}

	}

	@Getter
	public static class VoteResult {
		private String message;

		public VoteResult(String message) {
			this.message = message;
		}

	}

	@Getter
	public static class VoteResultDTO {
		private VoteCreator voteCreator;
		private ChatProfileDTO myProfile;
		private VoteRatio voteRatio;

		public VoteResultDTO(VoteCreator voteCreator, ChatProfileDTO myProfile, VoteRatio voteRatio) {
			this.voteCreator = voteCreator;
			this.myProfile = new ChatProfileDTO(myProfile.getNickname(), myProfile.getProfileImageUrl(),
				myProfile.getTeamName());
			this.voteRatio = new VoteRatio(voteRatio.option1VoteRatio, voteRatio.option2VoteRatio);
		}

	}

	@Getter
	public static class VoteCreator {
		private String creatorNickname;
		private Options creatorOptions;

		public VoteCreator(String creatorNickname, Options creatorOptions) {
			this.creatorNickname = creatorNickname;
			this.creatorOptions = new Options(creatorOptions.getQuestion(), creatorOptions.getOption1(),
				creatorOptions.getOption2());
		}
	}

	@Getter
	public static class VoteRatio {
		private int option1VoteRatio;
		private int option2VoteRatio;

		public VoteRatio(int option1VoteRatio, int option2VoteRatio) {
			this.option1VoteRatio = option1VoteRatio;
			this.option2VoteRatio = option2VoteRatio;
		}
	}
}