package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.FetusRecord;
import lombok.Data;

@Data
public class FetusRecordDTO {
    private Long recordId;
    private Integer week;
    private Double fetalWeight;
    private Double crownHeelLength;
    private Double headCircumference;

    public FetusRecordDTO(FetusRecord record) {
        this.recordId = record.getRecordId();
        this.week = record.getWeek();
        this.fetalWeight = record.getFetalWeight();
        this.crownHeelLength = record.getCrownHeelLength();
        this.headCircumference = record.getHeadCircumference();
    }
}
