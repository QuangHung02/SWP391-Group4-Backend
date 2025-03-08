package com.example.pregnancy_tracking.dto;

import lombok.Data;

@Data
public class FetusRecordDTO {
    private Long recordId;
    private Integer week;
    private Double fetalWeight;
    private Double crownHeelLength;
    private Double headCircumference;
    private String status;

    private Double minWeight;
    private Double maxWeight;
    private Double minLength;
    private Double maxLength;
    private Double minHeadCircumference;
    private Double maxHeadCircumference;
}
