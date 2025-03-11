package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.entity.PregnancyStandardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PregnancyStandardRepository extends JpaRepository<PregnancyStandard, PregnancyStandardId> {
    List<PregnancyStandard> findAllByOrderByIdWeekAsc();
}
