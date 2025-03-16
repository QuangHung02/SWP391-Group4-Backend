package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Fetuses")
@Getter
@Setter
public class Fetus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fetus_id")
    private Long fetusId;

    @ManyToOne
    @JoinColumn(name = "pregnancy_id", nullable = false)
    @JsonIgnoreProperties("fetuses")
    private Pregnancy pregnancy;

    @Column(name = "fetus_index")
    private Integer fetusIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private FetusStatus status;
}

