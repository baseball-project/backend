package com.example.baseballprediction.domain.chat.minigame.entity;

import java.time.LocalDateTime;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.chat.dto.ChatEventDTO.ChatProfileDTO;
import com.example.baseballprediction.domain.chat.minigame.dto.MiniGameVoteDTO.Options;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.domain.member.entity.Member;
import com.example.baseballprediction.global.constant.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class MiniGame extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mini_game_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	private String question;
	private String option1;
	private String option2;
	private LocalDateTime startedAt;
	@Enumerated(EnumType.STRING)
	private Status status;
	
	
	@Builder
    public MiniGame(Member member, Game game, String question, String option1, String option2, Status status) {
        this.member = member;
        this.game = game;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.status = status != null ? status :  Status.READY;
    }

	public void updateStatus(Status status) {
        this.status = status;
        if (Status.PROGRESS == status){
            this.startedAt = LocalDateTime.now();
        }
    }
    
    public ChatProfileDTO toChatProfileDTO() {
        return new ChatProfileDTO(
            this.member.getNickname(),
            this.member.getProfileImageUrl(),
            this.member.getTeam().getName() 
        );
    }
    
    public Options toOptions() {
        return new Options(
            this.question,
            this.option1,
            this.option2
        );
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

}