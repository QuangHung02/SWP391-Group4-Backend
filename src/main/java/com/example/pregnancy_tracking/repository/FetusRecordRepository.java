package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.FetusRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FetusRecordRepository extends JpaRepository<FetusRecord, Long> {
    List<FetusRecord> findByFetusFetusId(Long fetusId);
    List<FetusRecord> findByPregnancyPregnancyId(Long pregnancyId);
}
