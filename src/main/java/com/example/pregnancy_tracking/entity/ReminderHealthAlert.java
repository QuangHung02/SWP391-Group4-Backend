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
    @Column(name = "health_alert_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reminder_id")
    private Reminder reminder;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_type")
    private HealthType healthType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private SeverityLevel severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private AlertSource source;

    @Column(name = "notes")
    private String notes;
}
