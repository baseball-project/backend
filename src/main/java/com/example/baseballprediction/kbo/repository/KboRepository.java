package com.example.baseballprediction.kbo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.baseballprediction.domain.game.entity.Game;
import com.example.baseballprediction.global.constant.Status;

@Repository
public interface KboRepository extends JpaRepository<Game,Long>{
	
	Optional<Game> findByStatus(@Param("status")Status status);
	
}
