package com.example.baseballprediction.domain.chat.minigame.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;

public interface MiniGameRepository extends JpaRepository<MiniGame, Long> {
	

}