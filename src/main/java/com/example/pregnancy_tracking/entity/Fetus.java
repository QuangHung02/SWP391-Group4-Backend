package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Fetuses")
@Getter
@Setter
public class Fetus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fetusId;

    @ManyToOne
    @JoinColumn(name = "pregnancy_id", nullable = false)
    private Pregnancy pregnancy;

    private Integer fetusIndex;
}
