package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.MomStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MomStandardRepository extends JpaRepository<MomStandard, Integer> {
    Optional<MomStandard> findByWeek(Integer week);
}
