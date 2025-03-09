package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PregnancyStandard")
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

    public PregnancyStandardId getId() {
        return id;
    }

    public void setId(PregnancyStandardId id) {
        this.id = id;
    }

    public Integer getWeek() {
        return id.getWeek();
    }

    public Integer getFetusNumber() {
        return id.getFetusNumber();
    }

    public BigDecimal getMinWeight() {
        return minWeight;
    }

    public BigDecimal getMaxWeight() {
        return maxWeight;
    }

    public BigDecimal getMinLength() {
        return minLength;
    }

    public BigDecimal getMaxLength() {
        return maxLength;
    }

    public BigDecimal getMinHeadCircumference() {
        return minHeadCircumference;
    }

    public BigDecimal getMaxHeadCircumference() {
        return maxHeadCircumference;
    }

    public BigDecimal getAvgWeight() {
        return avgWeight;
    }
}
