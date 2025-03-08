package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.MotherRecord;
import com.example.pregnancy_tracking.service.MotherRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mother-records") // Nhóm API về hồ sơ mẹ
public class MotherRecordController {

    @Autowired
    private MotherRecordService motherRecordService;

    @Operation(summary = "Create a Mother Record", description = "Creates a new mother record linked to a pregnancy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{pregnancyId}")
    public ResponseEntity<MotherRecord> createRecord(@PathVariable Long pregnancyId,
                                                     @RequestBody MotherRecord motherRecord) {
        MotherRecord createdRecord = motherRecordService.createRecord(pregnancyId, motherRecord);
        return ResponseEntity.ok(createdRecord);
    }

    @Operation(summary = "Get Mother Records by Pregnancy ID", description = "Retrieves all mother records associated with a specific pregnancy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No records found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{pregnancyId}")
    public ResponseEntity<List<MotherRecord>> getRecordsByPregnancyId(@PathVariable Long pregnancyId) {
        List<MotherRecord> records = motherRecordService.getRecordsByPregnancyId(pregnancyId);
        return ResponseEntity.ok(records);
    }
}
