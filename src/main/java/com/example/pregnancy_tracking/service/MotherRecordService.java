package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.ReminderHealthAlertDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.MotherRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MotherRecordService {
    @Autowired
    private MotherRecordRepository motherRecordRepository;

    @Autowired
    private StandardService standardService;

    @Autowired
    private ReminderHealthAlertService reminderHealthAlertService;

    @Transactional
    public MotherRecord createRecord(Long pregnancyId, MotherRecord record) {
        if (record.getMotherHeight() != null && record.getMotherHeight() > 0
                && record.getMotherWeight() != null) {
            double heightInMeters = record.getMotherHeight() / 100.0;
            double bmi = record.getMotherWeight() / (heightInMeters * heightInMeters);
            record.setMotherBmi(Math.round(bmi * 100.0) / 100.0);
        }
        return motherRecordRepository.save(record);
    }

    public List<MotherRecord> getRecordsByPregnancyId(Long pregnancyId) {
        return motherRecordRepository.findByPregnancyPregnancyId(pregnancyId);
    }

    @Transactional
    public void checkMotherHealth(MotherRecord record) {
        Optional<MomStandard> standardOpt = standardService.getMomStandard(record.getWeek());

        standardOpt.ifPresent(standard -> {
            double bmi = record.getMotherBmi() != null ? record.getMotherBmi() : 0.0;
            if (bmi < standard.getMinBmi() || bmi > standard.getMaxBmi()) {
                HealthType healthType = (bmi < standard.getMinBmi()) ? HealthType.LOW_WEIGHT : HealthType.HIGH_BMI;

                ReminderHealthAlertDTO healthAlertDTO = new ReminderHealthAlertDTO(
                        null,
                        record.getPregnancy().getPregnancyId(),
                        healthType.name(),
                        SeverityLevel.MEDIUM.name(),
                        AlertSource.MOTHER_RECORDS.name(),
                        "Mother's BMI is outside the standard range."
                );

                reminderHealthAlertService.createHealthAlert(record.getPregnancy().getPregnancyId(), healthAlertDTO);
            }
        });
    }
}
