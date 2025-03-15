package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderMedicalTask;
import com.example.pregnancy_tracking.entity.ReminderStatus;
import com.example.pregnancy_tracking.repository.ReminderMedicalTaskRepository;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReminderMedicalTaskService {
    private final ReminderMedicalTaskRepository taskRepository;
    private final ReminderRepository reminderRepository;

    public ReminderMedicalTaskService(ReminderMedicalTaskRepository taskRepository, ReminderRepository reminderRepository) {
        this.taskRepository = taskRepository;
        this.reminderRepository = reminderRepository;
    }

    public List<ReminderMedicalTaskDTO> getAllMedicalTasks() {
        return taskRepository.findAll().stream()
                .map(task -> new ReminderMedicalTaskDTO(
                        task.getTaskId(),
                        task.getReminder() != null ? task.getReminder().getReminderId() : null,
                        task.getWeek(),
                        task.getTaskType(),
                        task.getTaskName(),
                        task.getNotes(),
                        task.getReminder().getStatus().name()  // Use parent reminder's status
                ))
                .collect(Collectors.toList());
    }

    public List<ReminderMedicalTaskDTO> getTasksByReminder(Long reminderId) {
        return taskRepository.findByReminderReminderId(reminderId).stream()
                .map(task -> new ReminderMedicalTaskDTO(
                        task.getTaskId(),
                        task.getReminder().getReminderId(),
                        task.getWeek(),
                        task.getTaskType(),
                        task.getTaskName(),
                        task.getNotes(),
                        task.getReminder().getStatus().name()  // Use parent reminder's status
                ))
                .collect(Collectors.toList());
    }

    public ReminderMedicalTaskDTO createTask(ReminderMedicalTaskDTO dto) {
        ReminderMedicalTask task = new ReminderMedicalTask();
        task.setWeek(dto.getWeek());
        task.setTaskType(dto.getTaskType());
        task.setTaskName(dto.getTaskName());
        task.setNotes(dto.getNotes());

        if (dto.getReminderId() != null) {
            Optional<Reminder> reminderOpt = reminderRepository.findById(dto.getReminderId());
            reminderOpt.ifPresent(task::setReminder);
        }

        task = taskRepository.save(task);
        return new ReminderMedicalTaskDTO(
                task.getTaskId(),
                task.getReminder() != null ? task.getReminder().getReminderId() : null,
                task.getWeek(),
                task.getTaskType(),
                task.getTaskName(),
                task.getNotes(),
                task.getReminder().getStatus().name()  // Use parent reminder's status
        );
    }

    public ReminderMedicalTaskDTO updateTaskStatus(Long taskId, String status) {
        ReminderMedicalTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        Reminder reminder = task.getReminder();
        if (reminder == null) {
            throw new RuntimeException("Task has no associated reminder");
        }

        try {
            ReminderStatus newStatus = ReminderStatus.valueOf(status.toUpperCase());
            reminder.setStatus(newStatus);
            reminderRepository.save(reminder);
            
            return new ReminderMedicalTaskDTO(
                    task.getTaskId(),
                    reminder.getReminderId(),
                    task.getWeek(),
                    task.getTaskType(),
                    task.getTaskName(),
                    task.getNotes(),
                    reminder.getStatus().name()
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
