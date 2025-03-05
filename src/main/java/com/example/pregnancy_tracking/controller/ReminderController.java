package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders") // Nhóm API về nhắc nhở
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @Operation(summary = "Create a Reminder", description = "Creates a new reminder for a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("")
    public ResponseEntity<Reminder> createReminder(@Valid @RequestBody Reminder reminder) {
        Reminder createdReminder = reminderService.createReminder(reminder);
        return ResponseEntity.ok(createdReminder);
    }

    @Operation(summary = "Get Reminders by User ID", description = "Retrieves all reminders associated with a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminders retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No reminders found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<Reminder>> getRemindersByUser(@PathVariable Long userId) {
        List<Reminder> reminders = reminderService.getRemindersByUser(userId);
        return ResponseEntity.ok(reminders);
    }
}
