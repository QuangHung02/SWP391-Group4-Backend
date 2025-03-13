package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.PregnancyDTO;
import com.example.pregnancy_tracking.dto.PregnancyResponseDTO;
import com.example.pregnancy_tracking.entity.FetusStatus;
import com.example.pregnancy_tracking.entity.PregnancyStatus;
import com.example.pregnancy_tracking.service.PregnancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pregnancy_tracking.dto.PregnancyListDTO;
import java.util.List;

@RestController
@RequestMapping("/api/pregnancies")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
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
    public ResponseEntity<PregnancyListDTO> createPregnancy(@Valid @RequestBody PregnancyDTO pregnancyDTO) {
        PregnancyListDTO createdPregnancy = pregnancyService.createPregnancy(pregnancyDTO);
        return ResponseEntity.ok(createdPregnancy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PregnancyListDTO> updatePregnancy(@PathVariable Long id,
                                                   @Valid @RequestBody PregnancyDTO pregnancyDTO) {
        PregnancyListDTO updatedPregnancy = pregnancyService.updatePregnancy(id, pregnancyDTO);
        return ResponseEntity.ok(updatedPregnancy);
    }

    @Operation(summary = "Get pregnancy by ID", description = "Retrieves a pregnancy record by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregnancy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Pregnancy not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PregnancyListDTO> getPregnancyById(@PathVariable Long id) {
        PregnancyListDTO pregnancy = pregnancyService.getPregnancyById(id);
        return ResponseEntity.ok(pregnancy);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updatePregnancyStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            PregnancyStatus pregnancyStatus = PregnancyStatus.valueOf(status.toUpperCase());
            pregnancyService.updatePregnancyStatus(id, pregnancyStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get pregnancies by User ID", description = "Retrieves all pregnancies of a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregnancies retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PregnancyListDTO>> getPregnanciesByUserId(@PathVariable Long userId) {
        List<PregnancyListDTO> pregnancies = pregnancyService.getPregnanciesByUserId(userId);
        return ResponseEntity.ok(pregnancies);
    }

    @Operation(summary = "Get ongoing pregnancy by User ID",
            description = "Retrieves the full details of the ongoing pregnancy for a specific user, including fetus data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ongoing pregnancy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No ongoing pregnancy found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/ongoing/{userId}")
    public ResponseEntity<PregnancyListDTO> getOngoingPregnancyByUserId(@PathVariable Long userId) {
        PregnancyListDTO pregnancy = pregnancyService.getOngoingPregnancyByUserId(userId);
        return ResponseEntity.ok(pregnancy);
    }

    @Operation(summary = "Update fetus status", description = "Updates the status of a specific fetus.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetus status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Fetus not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PatchMapping("/fetus/{fetusId}/status")
    public ResponseEntity<Void> updateFetusStatus(
            @PathVariable Long fetusId,
            @RequestParam String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            FetusStatus fetusStatus = FetusStatus.valueOf(status.toUpperCase());
            pregnancyService.updateFetusStatus(fetusId, fetusStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
