package com.example.pregnancy_tracking.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FetusRecordResponseDTO {
    private Long recordId;
    private Integer week;
    private BigDecimal fetalWeight;
    private BigDecimal femurLength;
    private BigDecimal headCircumference;
    private LocalDateTime createdAt;
    private Long fetusId;
}