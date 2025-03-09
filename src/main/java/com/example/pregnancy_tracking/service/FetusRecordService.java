package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.FetusRecordDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FetusRecordService {
    @Autowired
    private FetusRecordRepository fetusRecordRepository;

    @Autowired
    private FetusRepository fetusRepository;

    @Autowired
    private PregnancyRepository pregnancyRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private PregnancyStandardRepository pregnancyStandardRepository;

    @Autowired
    private ReminderHealthAlertRepository reminderHealthAlertRepository;


    public List<FetusRecordDTO> getRecordsByFetusId(Long fetusId) {
        List<FetusRecord> records = fetusRecordRepository.findByFetusFetusIdOrderByWeekAsc(fetusId);
        return records.stream().map(FetusRecordDTO::new).collect(Collectors.toList());
    }

    public FetusRecord createRecord(Long fetusId, FetusRecordDTO recordDTO) {
        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Fetus not found"));

        Pregnancy pregnancy = fetus.getPregnancy();
        int week = pregnancy.getGestationalWeeks();

        boolean exists = fetusRecordRepository.existsByFetusFetusIdAndWeek(fetusId, week);
        if (exists) {
            throw new IllegalArgumentException("FetusRecord for this week already exists.");
        }

        FetusRecord record = new FetusRecord();
        record.setFetus(fetus);
        record.setWeek(week);
        record.setFetalWeight(recordDTO.getFetalWeight());
        record.setCrownHeelLength(recordDTO.getCrownHeelLength());
        record.setHeadCircumference(recordDTO.getHeadCircumference());

        return fetusRecordRepository.save(record);
    }
    public void updateRecordsForPregnancy(Long pregnancyId, Integer newWeeks) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));

        List<FetusRecord> records = fetusRecordRepository.findByFetusPregnancyPregnancyId(pregnancyId);

        for (FetusRecord record : records) {
            int adjustedWeek = newWeeks - (pregnancy.getGestationalWeeks() - record.getWeek());
            record.setWeek(Math.max(adjustedWeek, 1));
            fetusRecordRepository.save(record);
        }
    }
    public void checkFetusGrowth(FetusRecord record) {
        Integer fetusIndex = record.getFetus().getFetusIndex();
        Optional<PregnancyStandard> standardOpt =
                pregnancyStandardRepository.findByWeekAndFetusNumber(record.getWeek(), fetusIndex);

        if (standardOpt.isPresent()) {
            PregnancyStandard standard = standardOpt.get();
            SeverityLevel severity = null;

            if (record.getFetalWeight() != null) {
                double minThreshold = standard.getMinWeight() * 1.1;
                double maxThreshold = standard.getMaxWeight() * 0.9;
                if (record.getFetalWeight() <= minThreshold || record.getFetalWeight() >= maxThreshold) {
                    severity = SeverityLevel.LOW;
                }
                if (record.getFetalWeight() <= standard.getMinWeight() || record.getFetalWeight() >= standard.getMaxWeight()) {
                    severity = SeverityLevel.MEDIUM;
                }
            }

            if (record.getCrownHeelLength() != null) {
                double minThreshold = standard.getMinLength() * 1.1;
                double maxThreshold = standard.getMaxLength() * 0.9;
                if (record.getCrownHeelLength() <= minThreshold || record.getCrownHeelLength() >= maxThreshold) {
                    severity = SeverityLevel.LOW;
                }
                if (record.getCrownHeelLength() <= standard.getMinLength() || record.getCrownHeelLength() >= standard.getMaxLength()) {
                    severity = SeverityLevel.MEDIUM;
                }
            }

            if (record.getHeadCircumference() != null) {
                double minThreshold = standard.getMinHeadCircumference() * 1.1;
                double maxThreshold = standard.getMaxHeadCircumference() * 0.9;
                if (record.getHeadCircumference() <= minThreshold || record.getHeadCircumference() >= maxThreshold) {
                    severity = SeverityLevel.LOW;
                }
                if (record.getHeadCircumference() <= standard.getMinHeadCircumference() || record.getHeadCircumference() >= standard.getMaxHeadCircumference()) {
                    severity = SeverityLevel.MEDIUM;
                }
            }

            // Nếu có cảnh báo, tạo ReminderHealthAlert
            if (severity != null) {
                Reminder reminder = reminderRepository.findByPregnancyPregnancyId(record.getFetus().getPregnancy().getPregnancyId())
                        .stream().findFirst().orElseThrow(() -> new RuntimeException("No Reminder found"));

                ReminderHealthAlert alert = new ReminderHealthAlert();
                alert.setReminder(reminder);
                alert.setHealthType(HealthType.FETUS_GROWTH);
                alert.setSeverity(severity);
                alert.setSource(AlertSource.SYSTEM);
                alert.setNotes("Fetus growth measurement out of normal range.");
                reminderHealthAlertRepository.save(alert);

                record.setStatus(FetusRecordStatus.ISSUE);
                fetusRecordRepository.save(record);
            }
        }
    }

}
