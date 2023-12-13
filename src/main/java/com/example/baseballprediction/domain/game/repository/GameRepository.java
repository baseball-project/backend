package com.example.baseballprediction.domain.game.repository;

import com.example.baseballprediction.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
