package com.example.baseballprediction.domain.monthlyfairy.dto;

import java.util.List;

import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MonthlyFairyResponse {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StatisticsDTO {
		private List<FairyDTO> winMembers;
		private List<FairyDTO> loseMembers;
	}

	@Getter
	public static class FairyDTO {
		private int rank;
		private int voteRatio;

		private String nickname;
		private int winFairyCount;
		private int loseFairyCount;
		private String title;
		private String teamName;

		public FairyDTO(MonthlyFairy monthlyFairy) {
			this.rank = monthlyFairy.getRank();
			this.voteRatio = monthlyFairy.getVoteRatio();
			this.winFairyCount = monthlyFairy.getMember().getWinFairyCount();
			this.loseFairyCount = monthlyFairy.getMember().getLoseFairyCount();
			this.nickname = monthlyFairy.getMember().getNickname();
			this.title = monthlyFairy.getMember().getComment();
			this.teamName = monthlyFairy.getMember().getTeam().getName();
		}
	}
}
