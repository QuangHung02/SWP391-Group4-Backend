package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "PregnancyStandard")
@Getter
@Setter
public class PregnancyStandard {

    @EmbeddedId
    private PregnancyStandardId id;

    @Column(name = "min_weight")
    private BigDecimal minWeight;

    @Column(name = "max_weight")
    private BigDecimal maxWeight;

    @Column(name = "min_length")
    private BigDecimal minLength;

    @Column(name = "max_length")
    private BigDecimal maxLength;

    @Column(name = "min_head_circumference")
    private BigDecimal minHeadCircumference;

    @Column(name = "max_head_circumference")
    private BigDecimal maxHeadCircumference;

    @Column(name = "avg_weight")
    private BigDecimal avgWeight;
}
