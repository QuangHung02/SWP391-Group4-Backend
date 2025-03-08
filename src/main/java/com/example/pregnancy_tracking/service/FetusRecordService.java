package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.entity.FetusRecordStatus;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.repository.FetusRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FetusRecordService {
    @Autowired
    private FetusRecordRepository pregnancyRecordRepository;

    @Autowired
    private StandardService standardService;

public List<FetusRecord> getRecordsByFetusId(Long fetusId) {
    if (fetusId == null || fetusId <= 0) {
        throw new IllegalArgumentException("Fetus ID must be a positive number.");
    }

        return pregnancyRecordRepository.findByFetusFetusId(fetusId);
    }

    public List<FetusRecord> getRecordsByPregnancyId(Long pregnancyId) {
        return pregnancyRecordRepository.findByPregnancyPregnancyId(pregnancyId);
    }

    public FetusRecord saveRecord(FetusRecord record) {
        return pregnancyRecordRepository.save(record);
    }

    public void updateRecordsStatusByFetusId(Long fetusId, FetusRecordStatus newStatus) {
        List<FetusRecord> records = pregnancyRecordRepository.findByFetusFetusId(fetusId);
        for (FetusRecord record : records) {
            if (record.getStatus() == FetusRecordStatus.ACTIVE) {
                record.setStatus(newStatus);
                pregnancyRecordRepository.save(record);
            }
        }
    }

public FetusRecord createRecord(Long fetusId, FetusRecord record) {
    if (record == null || record.getFetus() == null) {
        throw new IllegalArgumentException("Pregnancy record and fetus must not be null.");
    }

        return pregnancyRecordRepository.save(record);
    }

    public void updateRecordsForPregnancy(Long pregnancyId, int newWeeks) {
        List<FetusRecord> records = pregnancyRecordRepository.findByPregnancyPregnancyId(pregnancyId);
        for (FetusRecord record : records) {
            int adjustedWeek = record.getWeek() + (newWeeks - record.getPregnancy().getGestationalWeeks());
            record.setWeek(Math.max(adjustedWeek, 1));
            pregnancyRecordRepository.save(record);
        }
    }
    public void checkFetusGrowth(FetusRecord record) {
        Integer fetusIndex = record.getFetus().getFetusIndex();

        Optional<PregnancyStandard> standardOpt =
                standardService.getPregnancyStandard(record.getWeek(), fetusIndex);

        standardOpt.ifPresent(standard -> {
            if (record.getFetalWeight() < standard.getMinWeight() || record.getFetalWeight() > standard.getMaxWeight()) {
            }
        });
    }


}
