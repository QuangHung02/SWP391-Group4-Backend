package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderDTO;
import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderStatus;
import com.example.pregnancy_tracking.entity.ReminderType;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {
    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
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
        Reminder reminder = new Reminder();
        reminder.setType(ReminderType.valueOf(reminderDTO.getType()));  // FIXED
        reminder.setReminderDate(reminderDTO.getReminderDate());
        reminder.setStatus(ReminderStatus.NOT_YET);

        Reminder savedReminder = reminderRepository.save(reminder);
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
    public ReminderDTO updateReminderStatus(Long id, String status) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setStatus(ReminderStatus.valueOf(status));
        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToDTO(updatedReminder);
    }

    @Transactional
    public void deleteReminder(Long id) {
        reminderRepository.deleteById(id);
    }

    private ReminderDTO convertToDTO(Reminder reminder) {
        return new ReminderDTO(
                reminder.getReminderId(),
                reminder.getUser().getId(),
                reminder.getPregnancy().getPregnancyId(),
                reminder.getType().name(),
                reminder.getReminderDate(),
                reminder.getStatus().name(),
                reminder.getCreatedAt()
        );
    }
}
