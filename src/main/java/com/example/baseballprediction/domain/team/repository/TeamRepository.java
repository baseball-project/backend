package com.example.baseballprediction.domain.team.repository;

import com.example.baseballprediction.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    Optional<Team> findByShortName(String shortName);
}
