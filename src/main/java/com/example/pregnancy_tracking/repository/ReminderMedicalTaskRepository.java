package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.ReminderMedicalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderMedicalTaskRepository extends JpaRepository<ReminderMedicalTask, Long> {
    List<ReminderMedicalTask> findByReminderReminderId(Long reminderId);
}