package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.ReminderHealthAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderHealthAlertRepository extends JpaRepository<ReminderHealthAlert, Long> {
    List<ReminderHealthAlert> findByReminderReminderId(Long reminderId);
}
