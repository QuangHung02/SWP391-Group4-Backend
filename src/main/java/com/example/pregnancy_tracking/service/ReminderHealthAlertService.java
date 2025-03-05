package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderStatus;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReminderHealthAlertService {
    @Autowired
    private ReminderRepository reminderRepository;

    // This service will handle health alert reminders for pregnancies

    public void sendHealthAlert(Long userId) {
        // Logic to send health alerts based on pregnancy data
        List<Reminder> reminders = reminderRepository.findByUserUserId(userId);
        
        for (Reminder reminder : reminders) {
            if (reminder.getStatus() == ReminderStatus.NOT_YET) {
                // Send alert logic here
                // For example, send an email or notification to the user
            }
        }
    }
}
