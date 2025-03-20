package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderDTO;
import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.dto.ReminderHealthAlertDTO;
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
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
import com.example.pregnancy_tracking.repository.ReminderHealthAlertRepository;
import java.util.ArrayList;

@Service
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final ReminderMedicalTaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PregnancyRepository pregnancyRepository;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReminderHealthAlertRepository reminderHealthAlertRepository;

    public ReminderService(ReminderRepository reminderRepository, ReminderMedicalTaskRepository taskRepository) {
        this.reminderRepository = reminderRepository;
        this.taskRepository = taskRepository;
    }

    public List<ReminderDTO> getAllReminders(Long userId) {
        return reminderRepository.findByUser_Id(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReminderDTO getReminderById(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));
        return convertToDTO(reminder);
    }

    @Transactional
    public ReminderDTO createReminder(ReminderDTO reminderDTO) {
        if (reminderDTO.getTasks() != null && !reminderDTO.getTasks().isEmpty()) {
            return createReminderWithTasks(reminderDTO);
        }

        Reminder reminder = new Reminder();
        User user = userRepository.findById(reminderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
                if ("HEALTH_ALERT".equals(reminderDTO.getType()) && 
                !membershipService.canAccessHealthAlerts(user.getId())) {
                throw new MembershipFeatureException("Tính năng cảnh báo sức khỏe yêu cầu gói Premium");
            }
        Pregnancy pregnancy = pregnancyRepository.findById(reminderDTO.getPregnancyId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ"));

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Pregnancy pregnancy = pregnancyRepository.findById(reminderDTO.getPregnancyId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ"));
    
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
                taskRepository.save(task);
            }
        }
    
        // Send notification after creating reminder
        String title = "Nhắc nhở mới";
        String body = String.format("Bạn có nhắc nhở mới cho tuần thai %d", reminderDTO.getTasks().get(0).getWeek());
        notificationService.sendMedicalTaskNotification(reminderDTO.getUserId(), title, body);
        
        return convertToDTO(savedReminder);
    }

    @Transactional
    public ReminderDTO updateReminder(Long id, ReminderDTO reminderDTO) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));

        if (reminderDTO.getType() != null) {
            reminder.setType(ReminderType.valueOf(reminderDTO.getType()));
        }
        if (reminderDTO.getReminderDate() != null) {
            reminder.setReminderDate(reminderDTO.getReminderDate());
        }
        if (reminderDTO.getStatus() != null) {
            reminder.setStatus(ReminderStatus.valueOf(reminderDTO.getStatus()));
        }

        if (reminderDTO.getTasks() != null && !reminderDTO.getTasks().isEmpty()) {
            taskRepository.deleteByReminder(reminder);
            
            for (ReminderMedicalTaskDTO taskDTO : reminderDTO.getTasks()) {
                ReminderMedicalTask task = new ReminderMedicalTask();
                task.setReminder(reminder);
                task.setWeek(taskDTO.getWeek());
                task.setTaskType(taskDTO.getTaskType());
                task.setTaskName(taskDTO.getTaskName());
                task.setNotes(taskDTO.getNotes());
                taskRepository.save(task);
            }
        }

        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToDTO(updatedReminder);
    }

    @Transactional
    public ReminderDTO updateReminderStatus(Long id, ReminderStatus status) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));
        if (reminder.getStatus() != ReminderStatus.DONE) {
            notificationService.sendMedicalTaskNotification(
                reminder.getUser().getId(),
                "Nhắc nhở mới",
                reminder.getType().toString()
            );
        }

        reminder.setStatus(status);
        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToDTO(updatedReminder);
    }

    @Transactional
    public void deleteReminder(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));

        taskRepository.deleteAll(taskRepository.findByReminderReminderId(id));

        reminderRepository.delete(reminder);
    }

    @Transactional
    public void deleteUserReminders(Long userId) {
        // Delete all reminders associated with the user
        reminderRepository.deleteByUserId(userId);
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

        List<ReminderHealthAlertDTO> healthAlerts = new ArrayList<>();
        if (ReminderType.HEALTH_ALERT.equals(reminder.getType())) {
            healthAlerts = reminderHealthAlertRepository.findByReminderReminderId(reminder.getReminderId())
                    .stream()
                    .map(alert -> new ReminderHealthAlertDTO(
                        alert.getId(),
                        alert.getReminder().getReminderId(),
                        alert.getHealthType().name(),
                        alert.getSeverity().name(),
                        alert.getSource().name(),
                        alert.getNotes()
                    ))
                    .collect(Collectors.toList());
        }

        return new ReminderDTO(
                reminder.getReminderId(),
                reminder.getUser().getId(),
                reminder.getPregnancy().getPregnancyId(),
                reminder.getType().name(),
                reminder.getReminderDate(),
                reminder.getStatus().name(),
                reminder.getCreatedAt(),
                tasks,
                healthAlerts
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
