package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.ReminderMedicalTask;
import com.example.pregnancy_tracking.repository.ReminderMedicalTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderMedicalTaskService {
    @Autowired
    private ReminderMedicalTaskRepository reminderMedicalTaskRepository;

    public ReminderMedicalTask createTask(ReminderMedicalTask task) {
        return reminderMedicalTaskRepository.save(task);
    }

    public List<ReminderMedicalTask> getTasksByReminderId(Long reminderId) {
        return reminderMedicalTaskRepository.findByReminderReminderId(reminderId);
    }
}
