package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.PregnancyRecordDTO;
import com.example.pregnancy_tracking.entity.PregnancyRecord;
import com.example.pregnancy_tracking.entity.PregnancyRecordStatus;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.service.PregnancyRecordService;
import com.example.pregnancy_tracking.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pregnancy-records")
public class PregnancyRecordController {
    @Autowired
    private PregnancyRecordService pregnancyRecordService;

    @Autowired
    private StandardService standardService;

    @PostMapping("/{fetusId}")
    public ResponseEntity<PregnancyRecord> createRecord(@PathVariable Long fetusId, @RequestBody PregnancyRecord record) {
        record.setStatus(PregnancyRecordStatus.ACTIVE);
        PregnancyRecord createdRecord = pregnancyRecordService.createRecord(record);
        return ResponseEntity.ok(createdRecord);
    }

    @GetMapping("/pregnancy/{pregnancyId}")
    public ResponseEntity<List<PregnancyRecordDTO>> getRecordsByPregnancy(@PathVariable Long pregnancyId) {
        List<PregnancyRecord> records = pregnancyRecordService.getRecordsByPregnancyId(pregnancyId);
        List<PregnancyRecordDTO> response = new ArrayList<>();

        for (PregnancyRecord record : records) {
            Integer fetusIndex = record.getFetus().getFetusIndex();
            Optional<PregnancyStandard> standardOpt =
                    standardService.getPregnancyStandard(record.getWeek(), fetusIndex);

            PregnancyRecordDTO dto = new PregnancyRecordDTO();
            dto.setRecordId(record.getRecordId());
            dto.setWeek(record.getWeek());
            dto.setFetalWeight(record.getFetalWeight());
            dto.setCrownHeelLength(record.getCrownHeelLength());
            dto.setHeadCircumference(record.getHeadCircumference());
            dto.setStatus(record.getStatus().name());

            standardOpt.ifPresent(standard -> {
                dto.setMinWeight(standard.getMinWeight());
                dto.setMaxWeight(standard.getMaxWeight());
                dto.setMinLength(standard.getMinLength());
                dto.setMaxLength(standard.getMaxLength());
                dto.setMinHeadCircumference(standard.getMinHeadCircumference());
                dto.setMaxHeadCircumference(standard.getMaxHeadCircumference());
            });

            response.add(dto);
        }

        return ResponseEntity.ok(response);
    }
}
