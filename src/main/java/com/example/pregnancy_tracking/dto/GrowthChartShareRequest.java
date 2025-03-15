package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.ChartType;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class GrowthChartShareRequest {
    private Set<ChartType> chartTypes;
    private String title;
    private String content;
}