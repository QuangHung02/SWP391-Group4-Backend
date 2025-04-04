package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderMedicalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderMedicalTaskRepository extends JpaRepository<ReminderMedicalTask, Long> {
    List<ReminderMedicalTask> findByReminderReminderId(Long reminderId);
    
    @Modifying
    @Query("DELETE FROM ReminderMedicalTask t WHERE t.reminder = :reminder")
    void deleteByReminder(Reminder reminder);
    List<ReminderMedicalTask> findByReminder_User_Id(Long userId);
}