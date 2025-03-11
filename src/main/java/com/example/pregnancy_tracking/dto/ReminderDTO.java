
package com.example.pregnancy_tracking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReminderDTO {
    private Long reminderId;
    private Long userId;
    private Long pregnancyId;
    private String type;
    private LocalDateTime reminderDate;
    private String status;
    private LocalDateTime createdAt;

    public ReminderDTO(Long reminderId, Long userId, Long pregnancyId, String type, LocalDateTime reminderDate, String status, LocalDateTime createdAt) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.pregnancyId = pregnancyId;
        this.type = type;
        this.reminderDate = reminderDate;
        this.status = status;
        this.createdAt = createdAt;
    }
}
