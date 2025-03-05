package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {
    @Autowired
    private ReminderService reminderService;

    @PostMapping("/create")
    public ResponseEntity<Reminder> createReminder(@RequestBody Reminder reminder) {
        Reminder createdReminder = reminderService.createReminder(reminder);
        return ResponseEntity.ok(createdReminder);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Reminder>> getRemindersByUser(@PathVariable Long userId) {
        List<Reminder> reminders = reminderService.getRemindersByUser(userId);
        return ResponseEntity.ok(reminders);
    }
}
