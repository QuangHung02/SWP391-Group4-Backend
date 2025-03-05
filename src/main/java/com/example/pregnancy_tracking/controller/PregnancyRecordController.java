package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.PregnancyRecordDTO;
import com.example.pregnancy_tracking.entity.PregnancyRecord;
import com.example.pregnancy_tracking.entity.PregnancyRecordStatus;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.service.PregnancyRecordService;
import com.example.pregnancy_tracking.service.StandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a Pregnancy Record", description = "Creates a pregnancy record for a specific fetus ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{fetusId}")
    public ResponseEntity<PregnancyRecord> createRecord(@PathVariable Long fetusId,
                                                        @RequestBody PregnancyRecord record) {
        record.setStatus(PregnancyRecordStatus.ACTIVE);
        PregnancyRecord createdRecord = pregnancyRecordService.createRecord(fetusId, record);
        return ResponseEntity.ok(createdRecord);
    }

    @Operation(summary = "Get Pregnancy Records by Pregnancy ID", description = "Retrieves all pregnancy records associated with a specific pregnancy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No records found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
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
