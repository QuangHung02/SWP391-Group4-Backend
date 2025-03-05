package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "MotherRecords")
@Getter
@Setter
public class MotherRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne
    @JoinColumn(name = "pregnancy_id", nullable = false)
    private Pregnancy pregnancy;

    private Integer week;
    private Double motherWeight;
    private Double motherHeight;
    private Double motherBmi;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

