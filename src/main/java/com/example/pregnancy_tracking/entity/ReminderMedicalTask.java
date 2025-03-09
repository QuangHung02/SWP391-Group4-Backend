package com.example.pregnancy_tracking.entity;

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
    private Reminder reminder;

    private Integer week;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    private String taskName;
    private String notes;
}
