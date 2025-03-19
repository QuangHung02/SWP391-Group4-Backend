package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.service.ReminderMedicalTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Lấy tất cả nhiệm vụ y tế", 
              description = "Lấy danh sách tất cả các nhiệm vụ y tế của người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách nhiệm vụ thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping
    public ResponseEntity<List<ReminderMedicalTaskDTO>> getMedicalTasks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return ResponseEntity.ok(service.getAllMedicalTasks(user.getId()));
    }

    @Operation(summary = "Lấy nhiệm vụ y tế theo nhắc nhở", 
              description = "Lấy danh sách nhiệm vụ y tế cho một nhắc nhở cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách nhiệm vụ thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhắc nhở"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{reminderId}")
    public ResponseEntity<List<ReminderMedicalTaskDTO>> getTasksByReminder(@PathVariable Long reminderId) {
        return ResponseEntity.ok(service.getTasksByReminder(reminderId));
    }

    @Operation(summary = "Tạo nhiệm vụ y tế mới", 
              description = "Tạo một nhiệm vụ y tế mới (không yêu cầu ID nhắc nhở).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo nhiệm vụ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping
    public ResponseEntity<ReminderMedicalTaskDTO> createTask(@RequestBody ReminderMedicalTaskDTO dto) {
        return ResponseEntity.ok(service.createTask(dto));
    }

    @Operation(summary = "Cập nhật trạng thái nhiệm vụ y tế", 
              description = "Cập nhật trạng thái của một nhiệm vụ y tế cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
            @ApiResponse(responseCode = "400", description = "Giá trị trạng thái không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhiệm vụ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ReminderMedicalTaskDTO> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        return ResponseEntity.ok(service.updateTaskStatus(taskId, status));
    }

    @Operation(summary = "Xóa nhiệm vụ y tế", 
              description = "Xóa một nhiệm vụ y tế cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa nhiệm vụ thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhiệm vụ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        service.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
