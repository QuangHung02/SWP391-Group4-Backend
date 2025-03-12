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

    @Column(nullable = false)
    private Integer week;

    @Column(nullable = false, length = 20)
    private String taskType;

    @Column(length = 255)
    private String taskName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReminderStatus status;
}
