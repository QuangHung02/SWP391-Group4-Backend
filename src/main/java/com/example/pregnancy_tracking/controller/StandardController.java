package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.StandardMedicalTask;
import com.example.pregnancy_tracking.service.StandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pregnancy_tracking.dto.PregnancyStandardDTO;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/standards")
@SecurityRequirement(name = "Bearer Authentication")
public class StandardController {
    private final StandardService standardService;

    public StandardController(StandardService standardService) {
        this.standardService = standardService;
    }

    @Operation(summary = "Get all standard medical tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all tasks"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/medical-tasks")
    public ResponseEntity<List<StandardMedicalTask>> getAllStandardMedicalTasks() {
        return ResponseEntity.ok(standardService.getAllStandardMedicalTasks());
    }

    @Operation(summary = "Get standard medical tasks by week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks for week"),
            @ApiResponse(responseCode = "404", description = "No tasks found for this week"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/medical-tasks/week/{week}")
    public ResponseEntity<List<StandardMedicalTask>> getStandardMedicalTasksByWeek(@PathVariable Integer week) {
        List<StandardMedicalTask> tasks = standardService.getStandardMedicalTasksByWeek(week);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Create a standard medical task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/medical-tasks")
    public ResponseEntity<StandardMedicalTask> createStandardMedicalTask(@RequestBody StandardMedicalTask task) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(standardService.createStandardMedicalTask(task));
    }

    @Operation(summary = "Delete a standard medical task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/medical-tasks/{taskId}")
    public ResponseEntity<Void> deleteStandardMedicalTask(@PathVariable Long taskId) {
        try {
            standardService.deleteStandardMedicalTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Generate weekly tasks for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "User or pregnancy not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/generate-tasks")
    public ResponseEntity<Void> generateWeeklyTasks(
            @RequestParam Long userId,
            @RequestParam Long pregnancyId,
            @RequestParam Integer currentWeek) {
        try {
            standardService.checkAndCreateWeeklyTasks(userId, pregnancyId, currentWeek);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get all pregnancy standards", 
              description = "Retrieves standard values for all gestational weeks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Standards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No standards found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/pregnancy/all")
    public ResponseEntity<List<PregnancyStandardDTO>> getAllPregnancyStandards() {
        return ResponseEntity.ok(standardService.getAllPregnancyStandards());
    }

    @Operation(summary = "Get pregnancy standards by fetus number", 
              description = "Retrieves all standard values for a specific number of fetuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Standards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No standards found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/pregnancy/fetus/{fetusNumber}")
    public ResponseEntity<List<PregnancyStandardDTO>> getPregnancyStandardsByFetusNumber(
            @PathVariable Integer fetusNumber) {
        List<PregnancyStandardDTO> standards = standardService.getPregnancyStandardsByFetusNumber(fetusNumber);
        if (standards.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(standards);
    }
    @Operation(summary = "Get pregnancy standards by week and fetus number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Standards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No standards found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/pregnancy")
    public ResponseEntity<PregnancyStandardDTO> getPregnancyStandard(
            @RequestParam Integer week,
            @RequestParam Integer fetusNumber) {
        return standardService.getPregnancyStandard(week, fetusNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get pregnancy standards with prediction line", 
              description = "Retrieves standards and prediction line for visualization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No data found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/pregnancy/fetus/{fetusNumber}/with-prediction")
    public ResponseEntity<Map<String, Object>> getPregnancyStandardsWithPrediction(
            @PathVariable Integer fetusNumber,
            @RequestParam Integer currentWeek) {
        Map<String, Object> result = standardService.getStandardsWithPrediction(fetusNumber, currentWeek);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}