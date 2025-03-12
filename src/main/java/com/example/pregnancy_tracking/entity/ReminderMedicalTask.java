
package com.example.pregnancy_tracking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ReminderMedicalTasks")
@Getter
@Setter
public class ReminderMedicalTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "reminder_id", nullable = false)
    @JsonIgnore
    private Reminder reminder;

    private Integer week;
    private String taskType;
    private String taskName;
    private String notes;

    @Enumerated(EnumType.STRING)
    private ReminderStatus status;
}
