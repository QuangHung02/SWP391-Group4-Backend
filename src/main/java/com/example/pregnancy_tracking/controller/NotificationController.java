package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Gửi thông báo test", description = "Gửi thông báo test đến user")
    @ApiResponse(responseCode = "200", description = "Thông báo đã được gửi")
    @PostMapping("/test/{userId}")
    public ResponseEntity<String> sendTestNotification(@PathVariable Long userId) {
        notificationService.sendMedicalTaskNotification(
            userId,
            "Test Notification",
            "This is a test notification"
        );
        return ResponseEntity.ok("Notification sent successfully");
    }
}