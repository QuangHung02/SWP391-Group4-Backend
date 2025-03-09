package com.example.pregnancy_tracking.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PregnancyDTO {
    private Long userId;
    private LocalDate examDate;
    private Integer gestationalWeeks;
    private Integer gestationalDays;
    private Integer totalFetuses;
}

