package com.example.baseballprediction.domain.gamevote.entity;

import org.hibernate.annotations.DynamicUpdate;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.domain.team.entity.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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
	@JoinColumn(name = "game_id")
	private Game game;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;
	
	
	@Builder
	public GameVote(Member member, Game game, Team team){
		this.member = member;
		this.game = game;
		this.team = team;
	}
	
	public void modifyTeam(Team team) {
		Team(team);
	}

	private void Team(Team team) {
		this.team = team;
	}

}
