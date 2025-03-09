package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.PregnancyStandard;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PregnancyStandardDTO {
    private Integer week;
    private Integer fetusNumber;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private BigDecimal minLength;
    private BigDecimal maxLength;
    private BigDecimal minHeadCircumference;
    private BigDecimal maxHeadCircumference;
    private BigDecimal avgWeight;

    public PregnancyStandardDTO(PregnancyStandard standard) {
        this.week = standard.getWeek();
        this.fetusNumber = standard.getFetusNumber();
        this.minWeight = standard.getMinWeight();
        this.maxWeight = standard.getMaxWeight();
        this.minLength = standard.getMinLength();
        this.maxLength = standard.getMaxLength();
        this.minHeadCircumference = standard.getMinHeadCircumference();
        this.maxHeadCircumference = standard.getMaxHeadCircumference();
        this.avgWeight = standard.getAvgWeight();
    }
}
