package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.ReminderDTO;
import com.example.pregnancy_tracking.entity.ReminderStatus;
import com.example.pregnancy_tracking.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;
import com.example.pregnancy_tracking.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/reminders")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderController {
    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Operation(summary = "Lấy tất cả nhắc nhở bao gồm các nhiệm vụ y tế")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách nhắc nhở thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping
    public ResponseEntity<List<ReminderDTO>> getReminders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return ResponseEntity.ok(reminderService.getAllReminders(user.getId()));
    }

    @Operation(summary = "Lấy nhắc nhở theo ID bao gồm các nhiệm vụ y tế")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy nhắc nhở"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhắc nhở"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.getReminderById(id));
    }

    @Operation(summary = "Tạo nhắc nhở mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo nhắc nhở thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping
    public ResponseEntity<ReminderDTO> createReminder(@RequestBody ReminderDTO reminderDTO) {
        ReminderDTO created = reminderService.createReminder(reminderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Tạo nhắc nhở với các nhiệm vụ y tế")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo nhắc nhở và nhiệm vụ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/with-tasks")
    public ResponseEntity<ReminderDTO> createReminderWithTasks(@RequestBody ReminderDTO reminderDTO) {
        ReminderDTO created = reminderService.createReminderWithTasks(reminderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Cập nhật nhắc nhở")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật nhắc nhở thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhắc nhở"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
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
    public ResponseEntity<ReminderDTO> updateReminderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {

        try {
            String status = statusUpdate.get("status");
            ReminderStatus reminderStatus = ReminderStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(reminderService.updateReminderStatus(id, reminderStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete a reminder including all related medical tasks")
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
