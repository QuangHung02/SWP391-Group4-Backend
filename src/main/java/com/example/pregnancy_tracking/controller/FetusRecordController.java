package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.FetusRecordDTO;
import com.example.pregnancy_tracking.dto.PregnancyStandardDTO;
import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.service.FetusRecordService;
import com.example.pregnancy_tracking.service.StandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fetus-records")
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

    @Operation(summary = "Get Pregnancy Standards", description = "Retrieves standard values for a specific gestational week and fetus index.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Standards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No standards found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/standards/{gestationalWeek}/{fetusIndex}")
    public ResponseEntity<PregnancyStandardDTO> getPregnancyStandard(@PathVariable Integer gestationalWeek,
                                                                     @PathVariable Integer fetusIndex) {
        PregnancyStandard standard = standardService.getPregnancyStandard(gestationalWeek, fetusIndex)
                .orElseThrow(() -> new RuntimeException("Standard data not found"));

        PregnancyStandardDTO response = new PregnancyStandardDTO(standard);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Pregnancy Standards", description = "Retrieves standard values for a specific gestational week and fetus index.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Standards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No standards found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/standards/all")
    public ResponseEntity<List<PregnancyStandardDTO>> getAllPregnancyStandards() {
        return ResponseEntity.ok(standardService.getAllPregnancyStandards());
    }
}

