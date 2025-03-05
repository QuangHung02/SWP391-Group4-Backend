package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.MotherRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MotherRecordService {
    @Autowired
    private MotherRecordRepository motherRecordRepository;

    @Autowired
    private StandardService standardService;

    public MotherRecord createRecord(Long pregnancyId, MotherRecord record) {
        if (record.getMotherHeight() != null && record.getMotherHeight() > 0) {
            double heightInMeters = record.getMotherHeight() / 100.0;
            double bmi = record.getMotherWeight() / (heightInMeters * heightInMeters);
            record.setMotherBmi(Math.round(bmi * 100.0) / 100.0);
        }

        return motherRecordRepository.save(record);
    }

    public List<MotherRecord> getRecordsByPregnancyId(Long pregnancyId) {
        return motherRecordRepository.findByPregnancyPregnancyId(pregnancyId);
    }
    @Autowired
    private ReminderService reminderService;

    public void checkMotherHealth(MotherRecord record) {
        Optional<MomStandard> standardOpt = standardService.getMomStandard(record.getWeek());

        standardOpt.ifPresent(standard -> {
            if (record.getMotherBmi() < standard.getMinBmi() || record.getMotherBmi() > standard.getMaxBmi()) {
                Reminder reminder = new Reminder();
                reminder.setUser(record.getPregnancy().getUser());
                reminder.setPregnancy(record.getPregnancy());
                reminder.setType(ReminderType.HEALTH_ALERT);
                reminder.setReminderDate(LocalDateTime.now());
                reminder.setStatus(ReminderStatus.NOT_YET);
                Reminder createdReminder = reminderService.createReminder(reminder);

                ReminderHealthAlert alert = new ReminderHealthAlert();
                alert.setReminder(createdReminder);
                alert.setHealthType(HealthType.HIGH_BMI);
                alert.setSeverity(SeverityLevel.MODERATE);
                alert.setSource(AlertSource.MOTHER_RECORDS);
                alert.setNotes("Chỉ số BMI của mẹ vượt mức tiêu chuẩn.");
                reminderService.createHealthAlert(alert);
            }
        });
    }


}
