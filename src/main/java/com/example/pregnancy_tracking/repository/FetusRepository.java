package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Fetus;
import com.example.pregnancy_tracking.entity.FetusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface FetusRepository extends JpaRepository<Fetus, Long> {
    List<Fetus> findByPregnancyPregnancyId(Long pregnancyId);
    List<Fetus> findByPregnancyPregnancyIdAndStatus(Long pregnancyId, FetusStatus status);
    
    @Query("SELECT COUNT(f) FROM Fetus f WHERE f.pregnancy.pregnancyId = :pregnancyId")
    int countByPregnancyId(@Param("pregnancyId") Long pregnancyId);
}
