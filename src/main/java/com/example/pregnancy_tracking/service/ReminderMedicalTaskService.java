
package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderMedicalTask;
import com.example.pregnancy_tracking.entity.ReminderStatus;
import com.example.pregnancy_tracking.repository.ReminderMedicalTaskRepository;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderMedicalTaskService {
    private final ReminderMedicalTaskRepository repository;
    private final ReminderRepository reminderRepository;

    public ReminderMedicalTaskService(ReminderMedicalTaskRepository repository, ReminderRepository reminderRepository) {
        this.repository = repository;
        this.reminderRepository = reminderRepository;
    }

    public List<ReminderMedicalTaskDTO> getAllMedicalTasks() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReminderMedicalTaskDTO> getTasksByReminder(Long reminderId) {
        return repository.findByReminderReminderId(reminderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReminderMedicalTaskDTO createTask(Long reminderId, ReminderMedicalTaskDTO dto) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        ReminderMedicalTask task = new ReminderMedicalTask();
        task.setReminder(reminder);
        task.setWeek(dto.getWeek());
        task.setTaskType(dto.getTaskType());
        task.setTaskName(dto.getTaskName());
        task.setNotes(dto.getNotes());
        task.setStatus(ReminderStatus.valueOf(dto.getStatus())); // Fix Enum mapping

        ReminderMedicalTask savedTask = repository.save(task);
        return convertToDTO(savedTask);
    }

    @Transactional
    public ReminderMedicalTaskDTO updateTaskStatus(Long taskId, String status) {
        ReminderMedicalTask task = repository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(ReminderStatus.valueOf(status));
        ReminderMedicalTask updatedTask = repository.save(task);
        return convertToDTO(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        repository.deleteById(taskId);
    }

    private ReminderMedicalTaskDTO convertToDTO(ReminderMedicalTask task) {
        return new ReminderMedicalTaskDTO(
                task.getTaskId(),
                task.getReminder().getReminderId(),
                task.getWeek(),
                task.getTaskType(),
                task.getTaskName(),
                task.getNotes(),
                task.getStatus().name()
        );
    }
}
