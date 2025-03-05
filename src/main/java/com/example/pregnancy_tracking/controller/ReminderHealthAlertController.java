package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.ReminderHealthAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-alerts")
public class ReminderHealthAlertController {
    @Autowired
    private ReminderHealthAlertService reminderHealthAlertService;

    @PostMapping("/{pregnancyId}")
    public ResponseEntity<String> sendHealthAlert(@PathVariable Long pregnancyId) {
        reminderHealthAlertService.sendHealthAlert(pregnancyId);
        return ResponseEntity.ok("Health alert sent successfully.");
    }
}
