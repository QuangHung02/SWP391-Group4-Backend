package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.PregnancyStandardDTO;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.entity.MomStandard;
import com.example.pregnancy_tracking.entity.PregnancyStandardId;
import com.example.pregnancy_tracking.repository.PregnancyStandardRepository;
import com.example.pregnancy_tracking.repository.MomStandardRepository;
import com.example.pregnancy_tracking.repository.StandardMedicalTaskRepository;
import com.example.pregnancy_tracking.dto.ReminderDTO;
import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.entity.StandardMedicalTask;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StandardService {
    @Autowired
    private PregnancyStandardRepository pregnancyStandardRepository;

    @Autowired
    private MomStandardRepository momStandardRepository;

    @Autowired
    private StandardMedicalTaskRepository standardMedicalTaskRepository;

    @Autowired
    private ReminderService reminderService;

    public List<StandardMedicalTask> getAllStandardMedicalTasks() {
        return standardMedicalTaskRepository.findAllByOrderByWeekAsc();
    }

    public List<StandardMedicalTask> getStandardMedicalTasksByWeek(Integer week) {
        return standardMedicalTaskRepository.findByWeek(week);
    }

    public StandardMedicalTask createStandardMedicalTask(StandardMedicalTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getWeek() == null || task.getTaskName() == null) {
            throw new IllegalArgumentException("Week and task name are required");
        }
        return standardMedicalTaskRepository.save(task);
    }

    public void deleteStandardMedicalTask(Long taskId) {
        standardMedicalTaskRepository.deleteById(taskId);
    }

    public List<PregnancyStandardDTO> getAllPregnancyStandards() {
        List<PregnancyStandard> standards = pregnancyStandardRepository.findAllByOrderByIdWeekAsc();
        return standards.stream().map(PregnancyStandardDTO::new).collect(Collectors.toList());
    }

    public Optional<MomStandard> getMomStandard(Integer week) {
        return momStandardRepository.findByWeek(week);
    }

    public void createReminderFromStandardTasks(Long userId, Long pregnancyId, Integer currentWeek) {
        if (userId == null || pregnancyId == null || currentWeek == null) {
            throw new IllegalArgumentException("userId, pregnancyId, and currentWeek cannot be null");
        }

        try {
            List<StandardMedicalTask> standardTasks = standardMedicalTaskRepository.findByWeek(currentWeek);
            
            if (!standardTasks.isEmpty()) {
                ReminderDTO reminderDTO = new ReminderDTO();
                reminderDTO.setUserId(userId);
                reminderDTO.setPregnancyId(pregnancyId);
                reminderDTO.setType("MEDICAL");
                reminderDTO.setReminderDate(LocalDate.now());
                
                List<ReminderMedicalTaskDTO> tasks = standardTasks.stream()
                    .map(standardTask -> new ReminderMedicalTaskDTO(
                        null,
                        null,
                        standardTask.getWeek(),
                        standardTask.getTaskType(),
                        standardTask.getTaskName(),
                        standardTask.getNotes(),
                        null
                    ))
                    .collect(Collectors.toList());
                
                reminderDTO.setTasks(tasks);
                reminderService.createReminderWithTasks(reminderDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create reminder from standard tasks: " + e.getMessage());
        }
    }

    public void checkAndCreateWeeklyTasks(Long userId, Long pregnancyId, Integer currentWeek) {
        List<ReminderDTO> existingReminders = reminderService.getAllReminders();
        boolean reminderExists = existingReminders.stream()
            .anyMatch(reminder -> 
                reminder.getUserId().equals(userId) &&
                reminder.getPregnancyId().equals(pregnancyId) &&
                reminder.getTasks().stream()
                    .anyMatch(task -> currentWeek.equals(task.getWeek()))
            );
        
        if (!reminderExists) {
            createReminderFromStandardTasks(userId, pregnancyId, currentWeek);
        }
    }
}  // End of class

