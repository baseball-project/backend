package com.example.baseballprediction.domain.chat.minigame.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.baseballprediction.domain.chat.minigame.entity.MiniGame;
import com.example.baseballprediction.global.constant.Status;

public interface MiniGameRepository extends JpaRepository<MiniGame, Long> {
	
	List<MiniGame> findByGameIdAndStatus(Long gameId, Status status);
	List<MiniGame> findByGameIdAndStatusOrderByCreatedAtAsc(Long gameId, Status status);
	List<MiniGame> findByGameIdAndStatusIn(Long gameId, Collection<Status> statuses);

}