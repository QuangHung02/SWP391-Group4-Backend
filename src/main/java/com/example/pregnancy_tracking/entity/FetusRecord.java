package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FetusRecords")
@Getter
@Setter
public class FetusRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne
    @JoinColumn(name = "fetus_id", nullable = false)
    private Fetus fetus;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "fetal_weight", precision = 5, scale = 2)
    private BigDecimal fetalWeight;

    @Column(name = "crown_heel_length", precision = 5, scale = 2)
    private BigDecimal crownHeelLength;

    @Column(name = "head_circumference", precision = 5, scale = 2)
    private BigDecimal headCircumference;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
