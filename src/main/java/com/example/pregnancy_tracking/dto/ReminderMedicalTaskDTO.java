package com.example.pregnancy_tracking.dto;

import lombok.Data;

@Data
public class ReminderMedicalTaskDTO {
    private Long taskId;
    private Integer week;
    private String taskType;
    private String taskName;
    private String notes;
}
