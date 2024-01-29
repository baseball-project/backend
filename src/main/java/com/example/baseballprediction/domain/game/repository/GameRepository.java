package com.example.baseballprediction.domain.game.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.game.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
	List<Game> findAllByStartedAtBetween(LocalDateTime startedAtStart, LocalDateTime startedAtEnd);
}
