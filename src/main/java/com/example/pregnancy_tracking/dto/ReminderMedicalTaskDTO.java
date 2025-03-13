package com.example.pregnancy_tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderMedicalTaskDTO {
    private Long taskId;
    private Long reminderId;
    private Integer week;
    private String taskType;
    private String taskName;
    private String notes;
    private String status;
}
