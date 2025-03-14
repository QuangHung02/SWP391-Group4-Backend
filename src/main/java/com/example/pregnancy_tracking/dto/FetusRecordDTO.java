package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.FetusRecord;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal; 

@Data
public class FetusRecordDTO {
    private Long recordId;
    private Integer week;
    @DecimalMin(value = "0.0", message = "Value must be positive")
    private BigDecimal fetalWeight;
    @DecimalMin(value = "0.0", message = "Value must be positive")
    private BigDecimal crownHeelLength;
    @DecimalMin(value = "0.0", message = "Value must be positive")
    private BigDecimal headCircumference;

    public FetusRecordDTO() {}

    public FetusRecordDTO(FetusRecord record) {
        this.recordId = record.getRecordId();
        this.week = record.getWeek();
        this.fetalWeight = record.getFetalWeight();
        this.crownHeelLength = record.getCrownHeelLength();
        this.headCircumference = record.getHeadCircumference();
    }
}
