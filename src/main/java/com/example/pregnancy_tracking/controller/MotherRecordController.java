package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.MotherRecord;
import com.example.pregnancy_tracking.service.MotherRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mother-records")
public class MotherRecordController {
    @Autowired
    private MotherRecordService motherRecordService;

    @PostMapping("/{pregnancyId}")
    public ResponseEntity<MotherRecord> createRecord(@PathVariable Long pregnancyId,
                                                     @RequestBody MotherRecord record) {
        MotherRecord createdRecord = motherRecordService.createRecord(record);
        return ResponseEntity.ok(createdRecord);
    }

    @GetMapping("/{pregnancyId}")
    public ResponseEntity<List<MotherRecord>> getRecordsByPregnancyId(@PathVariable Long pregnancyId) {
        List<MotherRecord> records = motherRecordService.getRecordsByPregnancyId(pregnancyId);
        return ResponseEntity.ok(records);
    }
}
