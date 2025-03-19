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

    public List<ReminderMedicalTaskDTO> getAllMedicalTasks(Long userId) {
        return taskRepository.findByReminder_User_Id(userId).stream()
                .map(task -> new ReminderMedicalTaskDTO(
                        task.getTaskId(),
                        task.getReminder() != null ? task.getReminder().getReminderId() : null,
                        task.getWeek(),
                        task.getTaskType(),
                        task.getTaskName(),
                        task.getNotes(),
                        task.getReminder().getStatus().name()
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
                        task.getReminder().getStatus().name()
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
                task.getReminder().getStatus().name()
        );
    }

    public ReminderMedicalTaskDTO updateTaskStatus(Long taskId, String status) {
        ReminderMedicalTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhiệm vụ"));
        
        Reminder reminder = task.getReminder();
        if (reminder == null) {
            throw new RuntimeException("Nhiệm vụ không có nhắc nhở liên kết");
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
            throw new RuntimeException("Giá trị trạng thái không hợp lệ: " + status);
        }
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
