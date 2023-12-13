package com.example.baseballprediction.domain.game.entity;

import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int homeTeamScore;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int awayTeamScore;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "win_team_id", unique = false)
    private Team winTeam;

    @Builder
    public Game(Team homeTeam, Team awayTeam, Status status, LocalDateTime startedAt, int homeTeamScore, int awayTeamScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.status = status;
        this.startedAt = startedAt;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
    }
}
