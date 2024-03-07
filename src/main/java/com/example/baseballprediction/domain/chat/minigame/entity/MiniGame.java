package com.example.baseballprediction.domain.chat.minigame.entity;

import com.example.baseballprediction.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MiniGame {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mini_game_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_member_id")
	private Member creator;
	
	private String question;
	private String option1;
	private String option2;
	
	
	@Builder
	public MiniGame(Member creator,String question,String option1,String option2) {
		this.creator = creator;
		this.question = question;
		this.option1 = option1;
		this.option2 = option2;
	}

}