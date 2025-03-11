package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MomStandard")
@Getter
@Setter
public class MomStandard {
    @Id
    private Integer week;

    private Double minWeight;
    private Double maxWeight;
    private Double minBmi;
    private Double maxBmi;
}
