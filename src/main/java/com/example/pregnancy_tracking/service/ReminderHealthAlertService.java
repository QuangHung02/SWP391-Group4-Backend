package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderHealthAlertDTO;
import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderHealthAlert;
import com.example.pregnancy_tracking.entity.HealthType;
import com.example.pregnancy_tracking.entity.SeverityLevel;
import com.example.pregnancy_tracking.entity.AlertSource;
import com.example.pregnancy_tracking.repository.ReminderHealthAlertRepository;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderHealthAlertService {
    private final ReminderHealthAlertRepository repository;
    private final ReminderRepository reminderRepository;

    public ReminderHealthAlertService(ReminderHealthAlertRepository repository, ReminderRepository reminderRepository) {
        this.repository = repository;
        this.reminderRepository = reminderRepository;
    }

    public List<ReminderHealthAlertDTO> getAllHealthAlerts() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReminderHealthAlertDTO> getHealthAlertsByReminder(Long reminderId) {
        return repository.findByReminderReminderId(reminderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReminderHealthAlertDTO createHealthAlert(Long reminderId, ReminderHealthAlertDTO dto) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

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
        repository.deleteById(healthAlertId);
    }

    private ReminderHealthAlertDTO convertToDTO(ReminderHealthAlert alert) {
        return new ReminderHealthAlertDTO(
                alert.getHealthAlertId(),
                alert.getReminder().getReminderId(),
                alert.getHealthType().name(),
                alert.getSeverity().name(),
                alert.getSource().name(),
                alert.getNotes()
        );
    }
}
