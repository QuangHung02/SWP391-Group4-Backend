
package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.ReminderHealthAlertDTO;
import com.example.pregnancy_tracking.service.ReminderHealthAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-alerts")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderHealthAlertController {
    private final ReminderHealthAlertService service;

    public ReminderHealthAlertController(ReminderHealthAlertService service) {
        this.service = service;
    }

    @Operation(summary = "Get all health alerts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health alerts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ReminderHealthAlertDTO>> getHealthAlerts() {
        return ResponseEntity.ok(service.getAllHealthAlerts());
    }

    @Operation(summary = "Get health alerts by reminder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health alerts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No alerts found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{reminderId}")
    public ResponseEntity<List<ReminderHealthAlertDTO>> getHealthAlertsByReminder(@PathVariable Long reminderId) {
        return ResponseEntity.ok(service.getHealthAlertsByReminder(reminderId));
    }

    @Operation(summary = "Create a health alert")
    @PostMapping("/{reminderId}")
    public ResponseEntity<ReminderHealthAlertDTO> createHealthAlert(@PathVariable Long reminderId, @RequestBody ReminderHealthAlertDTO dto) {
        return ResponseEntity.ok(service.createHealthAlert(reminderId, dto));
    }

    @Operation(summary = "Delete a health alert")
    @DeleteMapping("/{healthAlertId}")
    public ResponseEntity<Void> deleteHealthAlert(@PathVariable Long healthAlertId) {
        service.deleteHealthAlert(healthAlertId);
        return ResponseEntity.noContent().build();
    }
}
