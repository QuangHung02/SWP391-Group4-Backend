package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.ReminderHealthAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderHealthAlertRepository extends JpaRepository<ReminderHealthAlert, Long> {
}
