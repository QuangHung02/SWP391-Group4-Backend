package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.FetusRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FetusRecordRepository extends JpaRepository<FetusRecord, Long> {
    List<FetusRecord> findByFetusPregnancyPregnancyId(Long pregnancyId);
    boolean existsByFetusFetusIdAndWeek(Long fetusId, int week);
    List<FetusRecord> findByFetusFetusIdOrderByWeekAsc(Long fetusId);

}
