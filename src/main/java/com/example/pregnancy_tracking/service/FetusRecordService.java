package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.entity.FetusRecordStatus;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.repository.FetusRecordRepository;
import com.example.pregnancy_tracking.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FetusRecordService {
    @Autowired
    private FetusRecordRepository fetusRecordRepository;

    @Autowired
    private StandardService standardService;

    public List<FetusRecord> getRecordsByFetusId(Long fetusId) {
        if (fetusId == null || fetusId <= 0) {
            throw new IllegalArgumentException("Fetus ID must be a positive number.");
        }
        return fetusRecordRepository.findByFetusFetusId(fetusId);
    }

    public List<FetusRecord> getRecordsByPregnancyId(Long pregnancyId) {
        return fetusRecordRepository.findByPregnancyPregnancyId(pregnancyId);
    }

    public FetusRecord saveRecord(FetusRecord record) {
        return fetusRecordRepository.save(record);
    }

    public FetusRecord createRecord(Long fetusId, FetusRecord record) {
        if (record == null || record.getFetus() == null) {
            throw new IllegalArgumentException("Record and fetus must not be null.");
        }
        return fetusRecordRepository.save(record);
    }

    public void updateRecordsStatusByFetusId(Long fetusId, FetusRecordStatus newStatus) {
        List<FetusRecord> records = fetusRecordRepository.findByFetusFetusId(fetusId);
        for (FetusRecord record : records) {
            if (record.getStatus() == FetusRecordStatus.ACTIVE) {
                record.setStatus(newStatus);
                fetusRecordRepository.save(record);
            }
        }
    }

    public void updateRecordsForPregnancy(Long pregnancyId, int newWeeks) {
        List<FetusRecord> records = fetusRecordRepository.findByPregnancyPregnancyId(pregnancyId);
        for (FetusRecord record : records) {
            int adjustedWeek = record.getWeek() + (newWeeks - record.getPregnancy().getGestationalWeeks());
            record.setWeek(Math.max(adjustedWeek, 1));
            fetusRecordRepository.save(record);
        }
    }

    public void checkFetusGrowth(FetusRecord record) {
        Integer fetusIndex = record.getFetus().getFetusIndex();
        Optional<PregnancyStandard> standardOpt =
                standardService.getPregnancyStandard(record.getWeek(), fetusIndex);
        standardOpt.ifPresent(standard -> {
            if (record.getFetalWeight() != null &&
                    (record.getFetalWeight() < standard.getMinWeight() || record.getFetalWeight() > standard.getMaxWeight())) {
                record.setStatus(FetusRecordStatus.ISSUE);
                fetusRecordRepository.save(record);
            }
        });
    }
}
