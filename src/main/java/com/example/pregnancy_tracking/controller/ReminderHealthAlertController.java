package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.ReminderHealthAlert;
import com.example.pregnancy_tracking.repository.ReminderHealthAlertRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-alerts")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderHealthAlertController {

    @Autowired
    private ReminderHealthAlertRepository reminderHealthAlertRepository;

    @Operation(summary = "Get all health alerts", description = "Retrieves all health alerts related to the user's pregnancy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health alerts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No alerts found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<ReminderHealthAlert>> getHealthAlertsByUser(@PathVariable Long userId) {
        List<ReminderHealthAlert> alerts = reminderHealthAlertRepository.findByReminderUserId(userId);
        return ResponseEntity.ok(alerts);
    }
}
