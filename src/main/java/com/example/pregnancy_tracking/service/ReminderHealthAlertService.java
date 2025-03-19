package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderHealthAlertDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.ReminderHealthAlertRepository;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderHealthAlertService {
    private final ReminderHealthAlertRepository repository;
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;

    public ReminderHealthAlertService(
            ReminderHealthAlertRepository repository, 
            ReminderRepository reminderRepository,
            UserRepository userRepository) {
        this.repository = repository;
        this.reminderRepository = reminderRepository;
        this.userRepository = userRepository;
    }

    public List<ReminderHealthAlertDTO> getAllHealthAlerts() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return repository.findAll().stream()
                .filter(alert -> alert.getReminder().getUser().getId().equals(currentUser.getId()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReminderHealthAlertDTO> getHealthAlertsByReminder(Long reminderId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));

        if (!reminder.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền xem các cảnh báo này");
        }

        return repository.findByReminderReminderId(reminderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReminderHealthAlertDTO createHealthAlert(Long reminderId, ReminderHealthAlertDTO dto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));

        if (!reminder.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền tạo cảnh báo cho nhắc nhở này");
        }

        ReminderHealthAlert alert = new ReminderHealthAlert();
        alert.setReminder(reminder);
        alert.setHealthType(HealthType.valueOf(dto.getHealthType()));
        alert.setSeverity(SeverityLevel.valueOf(dto.getSeverity()));
        alert.setSource(AlertSource.valueOf(dto.getSource()));
        alert.setNotes(dto.getNotes());

        ReminderHealthAlert savedAlert = repository.save(alert);
        return convertToDTO(savedAlert);
    }

    @Transactional
    public void deleteHealthAlert(Long healthAlertId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        ReminderHealthAlert alert = repository.findById(healthAlertId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cảnh báo sức khỏe"));

        if (!alert.getReminder().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền xóa cảnh báo này");
        }

        repository.deleteById(healthAlertId);
    }

    private ReminderHealthAlertDTO convertToDTO(ReminderHealthAlert alert) {
        return new ReminderHealthAlertDTO(
                alert.getId(),          
                alert.getReminder().getReminderId(),
                alert.getHealthType().name(),
                alert.getSeverity().name(),
                alert.getSource().name(),
                alert.getNotes()
        );
    }
}