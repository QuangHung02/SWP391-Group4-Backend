package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.FetusRecordDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    public FetusRecord createRecord(Long fetusId, FetusRecordDTO recordDTO) {
        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Fetus not found"));

        Pregnancy pregnancy = fetus.getPregnancy();
        int week = pregnancy.getGestationalWeeks();
        System.out.println("Pregnancy gestational weeks: " + week);

        if (fetusRecordRepository.existsByFetusFetusIdAndWeek(fetusId, week)) {
            throw new IllegalArgumentException("FetusRecord for this week already exists.");
        }

        FetusRecord record = new FetusRecord();
        record.setFetus(fetus);
        record.setWeek(week);
        record.setFetalWeight(recordDTO.getFetalWeight() != null ? recordDTO.getFetalWeight() : BigDecimal.ZERO);
        record.setCrownHeelLength(recordDTO.getCrownHeelLength() != null ? recordDTO.getCrownHeelLength() : BigDecimal.ZERO);
        record.setHeadCircumference(recordDTO.getHeadCircumference() != null ? recordDTO.getHeadCircumference() : BigDecimal.ZERO);

        return fetusRecordRepository.save(record);
    }

    @Transactional
    public void updateRecordsForPregnancy(Long pregnancyId, Integer newWeeks) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));

        List<FetusRecord> records = fetusRecordRepository.findByFetusPregnancyPregnancyId(pregnancyId);

        for (FetusRecord record : records) {
            int adjustedWeek = newWeeks - (pregnancy.getGestationalWeeks() - record.getWeek());
            record.setWeek(Math.max(adjustedWeek, 1));
        }
        fetusRecordRepository.saveAll(records);
    }

    @Transactional
    public void checkFetusGrowth(FetusRecord record) {
        Integer fetusNumber = record.getFetus().getFetusIndex();

        PregnancyStandardId standardId = new PregnancyStandardId(record.getWeek(), fetusNumber);
        Optional<PregnancyStandard> standardOpt = pregnancyStandardRepository.findById(standardId);

        if (standardOpt.isEmpty()) return;

        PregnancyStandard standard = standardOpt.get();
        SeverityLevel severity = null;

        if (record.getFetalWeight() != null) {
            severity = checkThreshold(record.getFetalWeight(), standard.getMinWeight(), standard.getMaxWeight(), severity);
        }

        if (record.getCrownHeelLength() != null) {
            severity = checkThreshold(record.getCrownHeelLength(), standard.getMinLength(), standard.getMaxLength(), severity);
        }

        if (record.getHeadCircumference() != null) {
            severity = checkThreshold(record.getHeadCircumference(), standard.getMinHeadCircumference(), standard.getMaxHeadCircumference(), severity);
        }

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

            Fetus fetus = record.getFetus();
            fetus.setStatus(FetusStatus.ISSUE);
            fetusRecordRepository.save(record);
        }
    }

    private SeverityLevel checkThreshold(BigDecimal value, BigDecimal min, BigDecimal max, SeverityLevel currentSeverity) {
        if (min == null || max == null) return currentSeverity;

        BigDecimal minThreshold = min.multiply(BigDecimal.valueOf(1.1));
        BigDecimal maxThreshold = max.multiply(BigDecimal.valueOf(0.9));

        if (value.compareTo(minThreshold) <= 0 || value.compareTo(maxThreshold) >= 0) {
            return SeverityLevel.LOW;
        }
        if (value.compareTo(min) <= 0 || value.compareTo(max) >= 0) {
            return SeverityLevel.MEDIUM;
        }
        return currentSeverity;
    }

}
