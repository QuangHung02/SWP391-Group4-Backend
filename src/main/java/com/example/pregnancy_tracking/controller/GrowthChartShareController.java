package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.GrowthChartShare;
import com.example.pregnancy_tracking.entity.ChartType;
import com.example.pregnancy_tracking.service.FetusRecordService;
import com.example.pregnancy_tracking.dto.GrowthChartShareRequest;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Set;
import java.util.Map;

@RestController
@RequestMapping("/api/growth-charts")
@RequiredArgsConstructor
public class GrowthChartShareController {
    private final FetusRecordService fetusRecordService;

    @Operation(summary = "Get available chart types for a fetus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available charts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{fetusId}/available-charts")
    public ResponseEntity<Map<ChartType, Boolean>> getAvailableCharts(@PathVariable Long fetusId) {
        return ResponseEntity.ok(fetusRecordService.getAvailableChartTypes(fetusId));
    }

    @Operation(summary = "Share growth chart data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Growth chart shared successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/share/{fetusId}")
    public ResponseEntity<GrowthChartShare> shareGrowthChart(
            @PathVariable Long fetusId,
            @RequestBody GrowthChartShareRequest request) {
        return ResponseEntity.ok(fetusRecordService.createGrowthChartShare(
            fetusId, 
            request.getChartTypes(), 
            request.getTitle(), 
            request.getContent()
        ));
    }

    @Operation(summary = "Get shared chart data by post ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chart data retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Chart not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getChartDataForPost(@PathVariable Long postId) {
        return ResponseEntity.ok(fetusRecordService.getChartDataForDisplay(postId));
    }
}