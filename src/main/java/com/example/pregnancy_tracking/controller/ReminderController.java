package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.ReminderDTO;
import com.example.pregnancy_tracking.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderController {
    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Operation(summary = "Get all reminders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminders retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ReminderDTO>> getReminders() {
        return ResponseEntity.ok(reminderService.getAllReminders());
    }

    @Operation(summary = "Get a reminder by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder found"),
            @ApiResponse(responseCode = "404", description = "Reminder not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.getReminderById(id));
    }

    @Operation(summary = "Create a new reminder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reminder created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ReminderDTO> createReminder(@RequestBody ReminderDTO reminderDTO) {
        return ResponseEntity.ok(reminderService.createReminder(reminderDTO));
    }

    @Operation(summary = "Update a reminder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder updated successfully"),
            @ApiResponse(responseCode = "404", description = "Reminder not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReminderDTO> updateReminder(@PathVariable Long id, @RequestBody ReminderDTO reminderDTO) {
        return ResponseEntity.ok(reminderService.updateReminder(id, reminderDTO));
    }

    @Operation(summary = "Update reminder status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Reminder not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReminderDTO> updateReminderStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(reminderService.updateReminderStatus(id, status));
    }

    @Operation(summary = "Delete a reminder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reminder deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Reminder not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}