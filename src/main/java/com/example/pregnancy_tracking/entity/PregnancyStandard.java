package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PregnancyStandard")
@Getter
@Setter
public class PregnancyStandard {
    @Id
    private Integer week;

    private Integer fetusNumber;

    private Double minWeight;
    private Double maxWeight;
    private Double minLength;
    private Double maxLength;
    private Double minHeadCircumference;
    private Double maxHeadCircumference;
}
