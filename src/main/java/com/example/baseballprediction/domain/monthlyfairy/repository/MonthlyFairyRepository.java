package com.example.baseballprediction.domain.monthlyfairy.repository;

import com.example.baseballprediction.domain.monthlyfairy.entity.MonthlyFairy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyFairyRepository extends JpaRepository<MonthlyFairy, Long> {
}
