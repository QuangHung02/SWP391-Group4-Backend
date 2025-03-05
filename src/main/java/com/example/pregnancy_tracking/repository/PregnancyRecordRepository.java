package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.PregnancyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PregnancyRecordRepository extends JpaRepository<PregnancyRecord, Long> {
    List<PregnancyRecord> findByFetusFetusId(Long fetusId);
    List<PregnancyRecord> findByPregnancyPregnancyId(Long pregnancyId);
}
