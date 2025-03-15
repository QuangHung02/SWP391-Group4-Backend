package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.PregnancyStandard;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PregnancyStandardDTO {
    private Integer week;
    private Integer fetusNumber;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private BigDecimal avgWeight;
    private BigDecimal minLength;
    private BigDecimal maxLength;
    private BigDecimal avgLength;
    private BigDecimal minHeadCircumference;
    private BigDecimal maxHeadCircumference;
    private BigDecimal avgHeadCircumference;

    public PregnancyStandardDTO() {}

    public PregnancyStandardDTO(PregnancyStandard standard) {
        this.week = standard.getId().getWeek();
        this.fetusNumber = standard.getId().getFetusNumber();
        this.minWeight = standard.getMinWeight();
        this.maxWeight = standard.getMaxWeight();
        this.minLength = standard.getMinLength();
        this.maxLength = standard.getMaxLength();
        this.minHeadCircumference = standard.getMinHeadCircumference();
        this.maxHeadCircumference = standard.getMaxHeadCircumference();
        this.avgWeight = standard.getAvgWeight();
        this.avgLength = standard.getAvgLength();
        this.avgHeadCircumference = standard.getAvgHeadCircumference();
    }
}
