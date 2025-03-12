package com.example.pregnancy_tracking.dto;

import lombok.Data;

@Data
public class ReminderMedicalTaskDTO {
    private Long taskId;
    private Long reminderId;
    private Integer week;
    private String taskType;
    private String taskName;
    private String notes;
    private String status;

    public ReminderMedicalTaskDTO(Long taskId, Long reminderId, Integer week, String taskType, String taskName, String notes, String status) {
        this.taskId = taskId;
        this.reminderId = reminderId;
        this.week = week;
        this.taskType = taskType;
        this.taskName = taskName;
        this.notes = notes;
        this.status = status;
    }
}