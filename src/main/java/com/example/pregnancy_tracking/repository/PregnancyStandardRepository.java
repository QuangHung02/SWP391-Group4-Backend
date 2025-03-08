package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.PregnancyStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PregnancyStandardRepository extends JpaRepository<PregnancyStandard, Integer> {
    Optional<PregnancyStandard> findByWeekAndFetusNumber(Integer week, Integer fetusNumber);
}
