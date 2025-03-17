package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.service.ReminderMedicalTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pregnancy_tracking.entity.User;
import java.util.List;

@RestController
@RequestMapping("/api/reminders/tasks")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderMedicalTaskController {
    private final ReminderMedicalTaskService service;

    public ReminderMedicalTaskController(ReminderMedicalTaskService service) {
        this.service = service;
    }

    @Operation(summary = "Get all medical tasks")
    @GetMapping
    public ResponseEntity<List<ReminderMedicalTaskDTO>> getMedicalTasks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return ResponseEntity.ok(service.getAllMedicalTasks(user.getId()));
    }

    @Operation(summary = "Get medical tasks by reminder")
    @GetMapping("/{reminderId}")
    public ResponseEntity<List<ReminderMedicalTaskDTO>> getTasksByReminder(@PathVariable Long reminderId) {
        return ResponseEntity.ok(service.getTasksByReminder(reminderId));
    }

    @Operation(summary = "Create a medical task (no reminderId required)")
    @PostMapping
    public ResponseEntity<ReminderMedicalTaskDTO> createTask(@RequestBody ReminderMedicalTaskDTO dto) {
        return ResponseEntity.ok(service.createTask(dto));
    }

    @Operation(summary = "Update medical task status")
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ReminderMedicalTaskDTO> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        return ResponseEntity.ok(service.updateTaskStatus(taskId, status));
    }

    @Operation(summary = "Delete a medical task")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        service.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
