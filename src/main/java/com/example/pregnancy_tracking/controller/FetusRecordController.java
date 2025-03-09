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
import java.util.Optional;
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

    @Operation(summary = "Get All Fetus Records by Fetus ID", description = "Retrieves all fetus records sorted by gestational week for a specific fetus.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No records found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{fetusId}/all")
    public ResponseEntity<List<FetusRecordDTO>> getAllRecordsByFetusId(@PathVariable Long fetusId) {
        List<FetusRecordDTO> response = fetusRecordService.getAllRecordsByFetusId(fetusId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Pregnancy Standards by Fetus Number", description = "Retrieves standard values for pregnancies with the given fetus number (e.g., singleton, twin).")
    @GetMapping("/standards/fetus/{fetusNumber}")
    public ResponseEntity<List<PregnancyStandardDTO>> getPregnancyStandardsByFetusNumber(@PathVariable Integer fetusNumber) {
        List<PregnancyStandardDTO> response = standardService.getPregnancyStandardsByFetusNumber(fetusNumber);
        return ResponseEntity.ok(response);
    }


}

