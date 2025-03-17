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
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne
    @JoinColumn(name = "reminder_id", nullable = false)
    private Reminder reminder;

    @Column(name = "task_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String taskName;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "week")
    private Integer week;

    @Column(name = "notes", columnDefinition = "NVARCHAR(MAX)")
    private String notes;
}
