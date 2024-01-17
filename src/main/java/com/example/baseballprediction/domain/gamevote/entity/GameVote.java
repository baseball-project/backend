package com.example.baseballprediction.domain.gamevote.entity;

import org.hibernate.annotations.DynamicUpdate;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.reply.entity.Reply;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.ReplyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class GameVote extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_vote_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@Builder
	public GameVote(Member member, Team team, Game game) {
		this.member = member;
		this.team = team;
		this.game = game;
	}
	
	public void modifyTeam(Team team) {
		setTeam(team);
	}
	
	private void setTeam(Team team) {
		this.team = team;
	}
	
}
