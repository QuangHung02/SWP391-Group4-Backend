package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.FetusStatus;
import com.example.pregnancy_tracking.entity.PregnancyStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PregnancyListDTO {
    private Long pregnancyId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate examDate;
    private Integer gestationalWeeks;
    private Integer gestationalDays;
    private PregnancyStatus status;
    private LocalDateTime lastUpdatedAt;
    private LocalDateTime createdAt;
    private Integer totalFetuses;
    private List<FetusDTO> fetuses;

    @Data
    public static class FetusDTO {
        private Long fetusId;
        private Integer fetusIndex;
        private FetusStatus status;
    }
}