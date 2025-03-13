package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dbo.StandardMedicalTasks")
@Getter
@Setter
public class StandardMedicalTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "task_type", length = 20)
    private String taskType;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}