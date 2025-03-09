package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.PregnancyStandard;
import lombok.Data;

@Data
public class PregnancyStandardDTO {
    private Integer week;
    private Integer fetusNumber;
    private Double minWeight;
    private Double maxWeight;
    private Double minLength;
    private Double maxLength;
    private Double minHeadCircumference;
    private Double maxHeadCircumference;

    public PregnancyStandardDTO(PregnancyStandard standard) {
        this.week = standard.getWeek();
        this.fetusNumber = standard.getFetusNumber();
        this.minWeight = standard.getMinWeight();
        this.maxWeight = standard.getMaxWeight();
        this.minLength = standard.getMinLength();
        this.maxLength = standard.getMaxLength();
        this.minHeadCircumference = standard.getMinHeadCircumference();
        this.maxHeadCircumference = standard.getMaxHeadCircumference();
    }
}
