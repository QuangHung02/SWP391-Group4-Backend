package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserId(Long id);
    List<Reminder> findByPregnancyPregnancyId(Long pregnancyId);
}
