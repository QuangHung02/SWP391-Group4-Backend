package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.FetusRecordDTO;
import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.service.FetusRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fetus-records")
public class FetusRecordController {

    @Autowired
    private FetusRecordService fetusRecordService;


    @Operation(summary = "Create a Fetus Record", description = "Creates a fetus record for a specific fetus ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{fetusId}")
    public ResponseEntity<FetusRecord> createRecord(@PathVariable Long fetusId,
                                                    @RequestBody @Valid FetusRecordDTO recordDTO) {
        FetusRecord createdRecord = fetusRecordService.createRecord(fetusId, recordDTO);
        return ResponseEntity.ok(createdRecord);
    }

    @Operation(summary = "Get Fetus Records by Fetus ID", description = "Retrieves all fetus records for a specific fetus.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No records found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{fetusId}")
    public ResponseEntity<List<FetusRecordDTO>> getRecordsByFetusId(@PathVariable Long fetusId) {
        List<FetusRecordDTO> response = fetusRecordService.getRecordsByFetusId(fetusId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Recorded Weeks by Fetus ID", 
              description = "Retrieves all gestational weeks that have records for a specific fetus.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weeks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No records found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{fetusId}/weeks")
    public ResponseEntity<List<Integer>> getWeeksByFetusId(@PathVariable Long fetusId) {
        List<Integer> weeks = fetusRecordService.getWeeksByFetusId(fetusId);
        if (weeks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(weeks);
    }

    @Operation(summary = "Get All Growth Measurements", 
              description = "Retrieves all growth measurements (head circumference, fetal weight, crown heel length) for a specific fetus ordered by week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No measurements found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{fetusId}/growth-data")
    public ResponseEntity<Map<String, List<Object[]>>> getAllGrowthData(@PathVariable Long fetusId) {
        Map<String, List<Object[]>> growthData = fetusRecordService.getAllGrowthData(fetusId);
        if (growthData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(growthData);
    }
}

