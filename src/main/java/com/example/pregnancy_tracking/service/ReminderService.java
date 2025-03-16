package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderDTO;
import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.PregnancyRepository;
import com.example.pregnancy_tracking.repository.ReminderMedicalTaskRepository;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final ReminderMedicalTaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PregnancyRepository pregnancyRepository;

    public ReminderService(ReminderRepository reminderRepository, ReminderMedicalTaskRepository taskRepository) {
        this.reminderRepository = reminderRepository;
        this.taskRepository = taskRepository;
    }

    public List<ReminderDTO> getAllReminders() {
        return reminderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReminderDTO getReminderById(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        return convertToDTO(reminder);
    }

    @Transactional
    public ReminderDTO createReminder(ReminderDTO reminderDTO) {
        if (reminderDTO.getTasks() != null && !reminderDTO.getTasks().isEmpty()) {
            return createReminderWithTasks(reminderDTO);
        }

        Reminder reminder = new Reminder();
        User user = userRepository.findById(reminderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pregnancy pregnancy = pregnancyRepository.findById(reminderDTO.getPregnancyId())
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));

        reminder.setUser(user);
        reminder.setPregnancy(pregnancy);
        reminder.setType(ReminderType.valueOf(reminderDTO.getType()));
        reminder.setReminderDate(reminderDTO.getReminderDate());
        reminder.setStatus(ReminderStatus.NOT_YET);

        Reminder savedReminder = reminderRepository.save(reminder);
        return convertToDTO(savedReminder);
    }

    @Transactional
    public ReminderDTO createReminderWithTasks(ReminderDTO reminderDTO) {
        Reminder reminder = new Reminder();
        User user = userRepository.findById(reminderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pregnancy pregnancy = pregnancyRepository.findById(reminderDTO.getPregnancyId())
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));
    
        reminder.setUser(user);
        reminder.setPregnancy(pregnancy);
        reminder.setType(ReminderType.valueOf(reminderDTO.getType().toUpperCase()));
        reminder.setReminderDate(reminderDTO.getReminderDate());
        reminder.setStatus(ReminderStatus.NOT_YET);
        
        Reminder savedReminder = reminderRepository.save(reminder);
    
        if (reminderDTO.getTasks() != null && !reminderDTO.getTasks().isEmpty()) {
            for (ReminderMedicalTaskDTO taskDTO : reminderDTO.getTasks()) {
                ReminderMedicalTask task = new ReminderMedicalTask();
                task.setReminder(savedReminder);
                task.setWeek(taskDTO.getWeek());
                task.setTaskType(taskDTO.getTaskType() != null ? taskDTO.getTaskType() : "");
                task.setTaskName(taskDTO.getTaskName());
                task.setNotes(taskDTO.getNotes() != null ? taskDTO.getNotes() : "");
                // Remove status setting
                taskRepository.save(task);
            }
        }
    
        return convertToDTO(savedReminder);
    }

    @Transactional
    public ReminderDTO updateReminder(Long id, ReminderDTO reminderDTO) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setType(ReminderType.valueOf(reminderDTO.getType()));
        reminder.setReminderDate(reminderDTO.getReminderDate());
        reminder.setStatus(ReminderStatus.valueOf(reminderDTO.getStatus()));

        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToDTO(updatedReminder);
    }

    @Transactional
    public ReminderDTO updateReminderStatus(Long id, ReminderStatus status) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setStatus(status);
        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToDTO(updatedReminder);
    }

    @Transactional
    public void deleteReminder(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        taskRepository.deleteAll(taskRepository.findByReminderReminderId(id));

        reminderRepository.delete(reminder);
    }

    private ReminderDTO convertToDTO(Reminder reminder) {
        List<ReminderMedicalTaskDTO> tasks = taskRepository.findByReminderReminderId(reminder.getReminderId())
                .stream()
                .map(task -> new ReminderMedicalTaskDTO(
                        task.getTaskId(),
                        task.getReminder().getReminderId(),
                        task.getWeek(),
                        task.getTaskType(),
                        task.getTaskName(),
                        task.getNotes(),
                        reminder.getStatus().name()
                ))
                .collect(Collectors.toList());

        return new ReminderDTO(
                reminder.getReminderId(),
                reminder.getUser().getId(),
                reminder.getPregnancy().getPregnancyId(),
                reminder.getType().name(),
                reminder.getReminderDate(),
                reminder.getStatus().name(),
                reminder.getCreatedAt(),
                tasks
        );
    }

    public boolean existsByUserIdAndPregnancyIdAndWeek(Long userId, Long pregnancyId, Integer week) {
        return reminderRepository.existsByUserIdAndPregnancyIdAndWeek(userId, pregnancyId, week);
    }

    public List<ReminderDTO> getRemindersByPregnancyId(Long pregnancyId) {
            return reminderRepository.findByPregnancy_PregnancyId(pregnancyId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }
}
