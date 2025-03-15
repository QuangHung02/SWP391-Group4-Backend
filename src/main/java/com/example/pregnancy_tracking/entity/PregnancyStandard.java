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

    @Column(name = "min_weight", precision = 7, scale = 2)
    private BigDecimal minWeight;

    @Column(name = "max_weight", precision = 7, scale = 2)
    private BigDecimal maxWeight;

    @Column(name = "avg_weight", precision = 7, scale = 2)
    private BigDecimal avgWeight;

    @Column(name = "min_length", precision = 7, scale = 2)
    private BigDecimal minLength;

    @Column(name = "max_length", precision = 7, scale = 2)
    private BigDecimal maxLength;

    @Column(name = "avg_length", precision = 7, scale = 2)
    private BigDecimal avgLength;

    @Column(name = "min_head_circumference", precision = 7, scale = 2)
    private BigDecimal minHeadCircumference;

    @Column(name = "max_head_circumference", precision = 7, scale = 2)
    private BigDecimal maxHeadCircumference;

    @Column(name = "avg_head_circumference", precision = 7, scale = 2)
    private BigDecimal avgHeadCircumference;

}
