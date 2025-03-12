package com.example.pregnancy_tracking.dto;

import lombok.Data;

@Data
public class MotherRecordDTO {
    private Long recordId;
    private Integer week;
    private Double motherWeight;
    private Double motherHeight;
    private Double motherBmi;
}
