
package com.example.pregnancy_tracking.dto;

import lombok.Data;

@Data
public class ReminderHealthAlertDTO {
    private Long healthAlertId;
    private Long reminderId;
    private String healthType;
    private String severity;
    private String source;
    private String notes;

    public ReminderHealthAlertDTO(Long healthAlertId, Long reminderId, String healthType, String severity, String source, String notes) {
        this.healthAlertId = healthAlertId;
        this.reminderId = reminderId;
        this.healthType = healthType;
        this.severity = severity;
        this.source = source;
        this.notes = notes;
    }
}
