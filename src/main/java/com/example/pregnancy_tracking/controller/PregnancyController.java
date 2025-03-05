package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.PregnancyDTO;
import com.example.pregnancy_tracking.dto.PregnancyStatusDTO;
import com.example.pregnancy_tracking.entity.Pregnancy;
import com.example.pregnancy_tracking.service.PregnancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pregnancies")
@CrossOrigin(origins = "*")  // Cho phép frontend truy cập API
public class PregnancyController {
    @Autowired
    private PregnancyService pregnancyService;

    @Operation(summary = "Create a new pregnancy", description = "Creates a new pregnancy record and returns the created object.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregnancy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("")
    public ResponseEntity<Pregnancy> createPregnancy(@Valid @RequestBody PregnancyDTO pregnancyDTO) {
        Pregnancy createdPregnancy = pregnancyService.createPregnancy(pregnancyDTO);
        return ResponseEntity.ok(createdPregnancy);
    }

    @Operation(summary = "Get pregnancy by ID", description = "Retrieves a pregnancy record by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregnancy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Pregnancy not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pregnancy> getPregnancyById(@PathVariable Long id) {
        Pregnancy pregnancy = pregnancyService.getPregnancyById(id);
        return ResponseEntity.ok(pregnancy);
    }

    @Operation(summary = "Update pregnancy", description = "Updates the details of an existing pregnancy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregnancy updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Pregnancy not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Pregnancy> updatePregnancy(@PathVariable Long id,
                                                     @Valid @RequestBody PregnancyDTO pregnancyDTO) {
        Pregnancy updatedPregnancy = pregnancyService.updatePregnancy(id, pregnancyDTO);
        return ResponseEntity.ok(updatedPregnancy);
    }

    @Operation(summary = "Update pregnancy status", description = "Updates the status of a pregnancy (ONGOING, COMPLETED, MISCARRIAGE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregnancy status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Pregnancy not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Pregnancy> updatePregnancyStatus(@PathVariable Long id,
                                                           @Valid @RequestBody PregnancyStatusDTO statusDTO) {
        Pregnancy updatedPregnancy = pregnancyService.updatePregnancyStatus(id, statusDTO.getStatus());
        return ResponseEntity.ok(updatedPregnancy);
    }
}
