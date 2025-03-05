package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderHealthAlert;
import com.example.pregnancy_tracking.repository.ReminderHealthAlertRepository;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {
    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderHealthAlertRepository reminderHealthAlertRepository;

public Reminder createReminder(Reminder reminder) {
    if (reminder == null || reminder.getUser() == null) {
        throw new IllegalArgumentException("Reminder and user must not be null.");
    }

        return reminderRepository.save(reminder);
    }

    public List<Reminder> getRemindersByUser(Long userId) {
        return reminderRepository.findByUserUserId(userId);
    }

public ReminderHealthAlert createHealthAlert(ReminderHealthAlert alert) {
    if (alert == null || alert.getReminder() == null) {
        throw new IllegalArgumentException("Health alert and reminder must not be null.");
    }


        return reminderHealthAlertRepository.save(alert);
    }
}
