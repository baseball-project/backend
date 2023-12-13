package com.example.baseballprediction.domain.game.entity;

import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "home_team_id")
    @Column(nullable = false)
    private Team homeTeam;

    @OneToOne
    @JoinColumn(name = "away_team_id")
    @Column(nullable = false)
    private Team awayTeam;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "home_team_score", nullable = false)
    @ColumnDefault("0")
    private int homeTeamScore;

    @Column(name = "away_team_score", nullable = false)
    @ColumnDefault("0")
    private int awayTeamScore;

    @OneToOne
    @JoinColumn(name = "win_team_id")
    private Team winTeam;
}
