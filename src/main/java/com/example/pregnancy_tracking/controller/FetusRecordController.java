package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.FetusRecordDTO;
import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.entity.FetusRecordStatus;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.service.FetusRecordService;
import com.example.pregnancy_tracking.service.StandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fetus-records")
@SecurityRequirement(name = "Bearer Authentication")
public class FetusRecordController {

    @Autowired
    private FetusRecordService fetusRecordService;

    @Autowired
    private StandardService standardService;

    @Operation(summary = "Create a Fetus Record", description = "Creates a fetus record for a specific fetus ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{fetusId}")
    public ResponseEntity<FetusRecord> createFetusRecord(@PathVariable Long fetusId,
                                                         @RequestBody FetusRecord record) {
        record.setStatus(FetusRecordStatus.ACTIVE);
        FetusRecord createdRecord = fetusRecordService.createRecord(fetusId, record);
        return ResponseEntity.ok(createdRecord);
    }

    @Operation(summary = "Get Fetus Records by Pregnancy ID", description = "Retrieves all fetus records associated with a specific pregnancy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No records found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/pregnancy/{pregnancyId}")
    public ResponseEntity<List<FetusRecordDTO>> getFetusRecordsByPregnancy(@PathVariable Long pregnancyId) {
        List<FetusRecord> records = fetusRecordService.getRecordsByPregnancyId(pregnancyId);
        List<FetusRecordDTO> response = new ArrayList<>();

        for (FetusRecord record : records) {
            response.add(convertToDTO(record));
        }

        return ResponseEntity.ok(response);
    }

    private FetusRecordDTO convertToDTO(FetusRecord record) {
        FetusRecordDTO dto = new FetusRecordDTO();
        dto.setRecordId(record.getRecordId());
        dto.setWeek(record.getWeek());
        dto.setFetalWeight(record.getFetalWeight());
        dto.setCrownHeelLength(record.getCrownHeelLength());
        dto.setHeadCircumference(record.getHeadCircumference());
        dto.setStatus(record.getStatus().name());

        Integer fetusIndex = record.getFetus().getFetusIndex();
        Optional<PregnancyStandard> standardOpt = standardService.getPregnancyStandard(record.getWeek(), fetusIndex);
        standardOpt.ifPresent(standard -> {
            dto.setMinWeight(standard.getMinWeight());
            dto.setMaxWeight(standard.getMaxWeight());
            dto.setMinLength(standard.getMinLength());
            dto.setMaxLength(standard.getMaxLength());
            dto.setMinHeadCircumference(standard.getMinHeadCircumference());
            dto.setMaxHeadCircumference(standard.getMaxHeadCircumference());
        });
        return dto;
    }
}
