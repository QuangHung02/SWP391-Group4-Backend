package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.Fetus;
import com.example.pregnancy_tracking.entity.PregnancyStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PregnancyResponseDTO {
    private Long pregnancyId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate examDate;
    private Integer gestationalWeeks;
    private Integer gestationalDays;
    private PregnancyStatus status;
    private List<Fetus> fetuses;
}
