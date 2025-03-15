package com.example.pregnancy_tracking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ReminderHealthAlerts")
@Getter
@Setter
public class ReminderHealthAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long healthAlertId;

    @ManyToOne
    @JoinColumn(name = "reminder_id", nullable = false)
    @JsonIgnore
    private Reminder reminder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HealthType healthType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeverityLevel severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSource source;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
