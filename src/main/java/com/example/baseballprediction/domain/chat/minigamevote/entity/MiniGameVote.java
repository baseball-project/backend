package com.example.baseballprediction.domain.chat.minigamevote.entity;


import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;
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
public class MiniGameVote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mini_game_vote_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mini_game_id", nullable = false)
	private MiniGame miniGame;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	 
	@Column(name = "vote_option", nullable = false)
	private int voteOption;
	
	@Builder
	public MiniGameVote(MiniGame miniGame,Member member,int voteOption) {
		this.miniGame = miniGame;
		this.member = member;
		this.voteOption = voteOption;
	}
	
}