package com.example.pregnancy_tracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReminderDTO {
    private Long reminderId;
    private Long userId;
    private Long pregnancyId;
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate reminderDate;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private List<ReminderMedicalTaskDTO> tasks;

    public ReminderDTO(Long reminderId, Long userId, Long pregnancyId, String type, LocalDate reminderDate, String status, LocalDateTime createdAt, List<ReminderMedicalTaskDTO> tasks) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.pregnancyId = pregnancyId;
        this.type = type;
        this.reminderDate = reminderDate;
        this.status = status;
        this.createdAt = createdAt;
        this.tasks = tasks;
    }
}
