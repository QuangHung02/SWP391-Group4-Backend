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
public class ReminderDTO {
    private Long reminderId;
    private Long userId;
    private Long pregnancyId;
    private String type;
    private LocalDate reminderDate;
    private String status;
    private LocalDateTime createdAt;
    private List<ReminderMedicalTaskDTO> tasks;
    private List<ReminderHealthAlertDTO> healthAlerts;

    public ReminderDTO(Long reminderId, Long userId, Long pregnancyId, 
                      String type, LocalDate reminderDate, String status, 
                      LocalDateTime createdAt, List<ReminderMedicalTaskDTO> tasks,
                      List<ReminderHealthAlertDTO> healthAlerts) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.pregnancyId = pregnancyId;
        this.type = type;
        this.reminderDate = reminderDate;
        this.status = status;
        this.tasks = tasks;
        this.createdAt = createdAt;
        this.healthAlerts = healthAlerts;
    }

    public List<ReminderHealthAlertDTO> getHealthAlerts() {
        return healthAlerts;
    }
}
