package com.example.baseballprediction.domain.chat.minigame.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;
import com.example.baseballprediction.global.constant.Status;

public interface MiniGameRepository extends JpaRepository<MiniGame, Long> {
	
	List<MiniGame> findByGameIdAndStatus(Long gameId, Status status);
	List<MiniGame> findByStatus(Status status);
    List<MiniGame> findByStatusOrderByCreatedAtAsc(Status status);
    List<MiniGame> findByGameIdAndStatusOrderByCreatedAtDesc(Long gameId, Status status);

}