package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.PregnancyRecord;
import com.example.pregnancy_tracking.entity.PregnancyRecordStatus;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.repository.PregnancyRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PregnancyRecordService {
    @Autowired
    private PregnancyRecordRepository pregnancyRecordRepository;

    @Autowired
    private StandardService standardService;

    public List<PregnancyRecord> getRecordsByFetusId(Long fetusId) {
        return pregnancyRecordRepository.findByFetusFetusId(fetusId);
    }

    public PregnancyRecord saveRecord(PregnancyRecord record) {
        return pregnancyRecordRepository.save(record);
    }

    public void updateRecordsStatusByFetusId(Long fetusId, PregnancyRecordStatus newStatus) {
        List<PregnancyRecord> records = pregnancyRecordRepository.findByFetusFetusId(fetusId);
        for (PregnancyRecord record : records) {
            if (record.getStatus() == PregnancyRecordStatus.ACTIVE) {
                record.setStatus(newStatus);
                pregnancyRecordRepository.save(record);
            }
        }
    }

    public PregnancyRecord createRecord(PregnancyRecord record) {
        return pregnancyRecordRepository.save(record);
    }

    public void updateRecordsForPregnancy(Long pregnancyId, int newWeeks) {
        List<PregnancyRecord> records = pregnancyRecordRepository.findByPregnancyPregnancyId(pregnancyId);
        for (PregnancyRecord record : records) {
            int adjustedWeek = record.getWeek() + (newWeeks - record.getPregnancy().getGestationalWeeks());
            record.setWeek(Math.max(adjustedWeek, 1));
            pregnancyRecordRepository.save(record);
        }
    }
    public void checkFetusGrowth(PregnancyRecord record) {
        Integer fetusIndex = record.getFetus().getFetusIndex();

        Optional<PregnancyStandard> standardOpt =
                standardService.getPregnancyStandard(record.getWeek(), fetusIndex);

        standardOpt.ifPresent(standard -> {
            if (record.getFetalWeight() < standard.getMinWeight() || record.getFetalWeight() > standard.getMaxWeight()) {
            }
        });
    }


}
