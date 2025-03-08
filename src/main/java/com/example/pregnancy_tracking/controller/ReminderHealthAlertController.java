package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.ReminderHealthAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health-alerts")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderHealthAlertController {

    @Autowired
    private ReminderHealthAlertService reminderHealthAlertService;

    @Operation(summary = "Send Health Alert", description = "Triggers a health alert for a specific pregnancy ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health alert sent successfully"),
            @ApiResponse(responseCode = "404", description = "Pregnancy not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{pregnancyId}")
    public ResponseEntity<Map<String, String>> sendHealthAlert(@PathVariable Long pregnancyId) {
        reminderHealthAlertService.sendHealthAlert(pregnancyId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Health alert sent successfully.");
        response.put("pregnancyId", pregnancyId.toString());

        return ResponseEntity.ok(response);
    }
}
