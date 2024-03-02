package com.example.baseballprediction.domain.member.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.SocialType;

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
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(length = 50, unique = true)
	private String username;

	@Column(length = 200, nullable = false)
	private String password;
	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false, name = "social")
	private SocialType socialType;
	@Column(length = 200, unique = true, nullable = false)
	private String nickname;

	@Column(length = 200)
	private String profileImageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@Column(length = 100, name = "member_comment")
	private String comment;

	@ColumnDefault("0")
	@Column(nullable = false)
	private Integer token;

	@ColumnDefault("1")
	@Column(nullable = false)
	private Integer level;

	@Transient
	private int winFairyCount;
	@Transient
	private int loseFairyCount;

	@Transient
	private boolean isNewMember;

	@Transient
	private int voteScore;

	@Transient
	private int voteRatio;

	@Builder
	public Member(Long id, String username, String password, String nickname, SocialType socialType,
		boolean isNewMember) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.socialType = socialType;
		this.isNewMember = isNewMember;
	}

	public void changeTeam(Team team) {
		this.team = team;
	}

	public void updateDetails(String profileImageUrl, String nickname, String comment) {
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.comment = comment;
	}

	public void setFairyCount(int winFairyCount, int loseFairyCount) {
		this.winFairyCount = winFairyCount;
		this.loseFairyCount = loseFairyCount;
	}

	public void setIsNewMember(boolean isNewMember) {
		this.isNewMember = isNewMember;
	}

	public void sumVoteScore(int voteScore) {
		this.voteScore += voteScore;
	}

	public void setVoteRatio(int voteRatio) {
		this.voteRatio = voteRatio;
	}
	
	public void addToken(int token) {
        this.token += token;
	}
}
