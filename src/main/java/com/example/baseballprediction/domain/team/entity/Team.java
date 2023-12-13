package com.example.baseballprediction.domain.team.entity;

import com.example.baseballprediction.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private int id;

    @Column(unique = true, nullable = false, length = 30)
    private String name;
    @Column(length = 200)
    private String logoUrl;
    @Column(length = 16)
    private String color;
}
