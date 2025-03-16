package com.example.pregnancy_tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    private Long reminderId;
    private Long userId;
    private Long pregnancyId;
    private String type;
    private LocalDate reminderDate;
    private String status;
    private LocalDateTime createdAt;
    private List<ReminderMedicalTaskDTO> tasks;

    public ReminderDTO(Long reminderId, Long userId, Long pregnancyId,
                      String type, LocalDate reminderDate, String status, 
                      List<ReminderMedicalTaskDTO> tasks) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.pregnancyId = pregnancyId;
        this.type = type;
        this.reminderDate = reminderDate;
        this.status = status;
        this.tasks = tasks;
        this.createdAt = LocalDateTime.now();
    }
}
