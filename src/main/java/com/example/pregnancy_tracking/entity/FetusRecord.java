package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FetusRecords", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"fetus_id", "week"}))
@Getter
@Setter
public class FetusRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fetus_id")
    private Fetus fetus;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "fetal_weight", precision = 10, scale = 2)
    private BigDecimal fetalWeight;

    @Column(name = "femur_length", precision = 10, scale = 2)
    private BigDecimal femurLength;

    @Column(name = "head_circumference", precision = 10, scale = 2)
    private BigDecimal headCircumference;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
