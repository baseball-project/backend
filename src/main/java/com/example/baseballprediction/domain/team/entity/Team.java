package com.example.baseballprediction.domain.team.entity;

import com.example.baseballprediction.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private int id;

    @Column(unique = true, nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 10)
    private String shortName;
    @Column(length = 200)
    private String logoUrl;
    @Column(length = 16)
    private String color;
}
