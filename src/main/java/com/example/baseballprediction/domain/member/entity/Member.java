package com.example.baseballprediction.domain.member.entity;

import com.example.baseballprediction.domain.BaseEntity;
import com.example.baseballprediction.domain.team.entity.Team;
import com.example.baseballprediction.global.constant.SocialType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 100, unique = true)
    private String username;

    @Column(length = 200, nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private SocialType socialType;
    @Column(length = 20, unique = true, nullable = false)
    private String nickname;

    @Column(length = 200)
    private String profileImageUrl;

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(length = 100, name = "member_comment")
    private String comment;

    @ColumnDefault("0")
    @Column(nullable = false)
    private int token;

    @ColumnDefault("1")
    @Column(nullable = false)
    private int level;
}
