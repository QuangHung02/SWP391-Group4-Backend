package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.MotherRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotherRecordRepository extends JpaRepository<MotherRecord, Long> {
    List<MotherRecord> findByPregnancyPregnancyId(Long pregnancyId);
}
