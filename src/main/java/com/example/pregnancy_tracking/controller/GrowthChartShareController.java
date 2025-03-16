package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.GrowthChartShare;
import com.example.pregnancy_tracking.entity.ChartType;
import com.example.pregnancy_tracking.service.FetusRecordService;
import com.example.pregnancy_tracking.dto.GrowthChartShareRequest;
import com.example.pregnancy_tracking.service.MembershipService;
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import com.example.pregnancy_tracking.entity.User;

@RestController
@RequestMapping("/api/growth-charts")
@SecurityRequirement(name = "Bearer Authentication")
public class GrowthChartShareController {
    private final FetusRecordService fetusRecordService;
    private final MembershipService membershipService;

    public GrowthChartShareController(FetusRecordService fetusRecordService, 
                                    MembershipService membershipService) {
        this.fetusRecordService = fetusRecordService;
        this.membershipService = membershipService;
    }
    @GetMapping("/{fetusId}/available-charts")
    public ResponseEntity<Map<ChartType, Boolean>> getAvailableCharts(
            @PathVariable Long fetusId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        return ResponseEntity.ok(fetusRecordService.getAvailableChartTypes(fetusId, userId));
    }

    @PostMapping("/share/{fetusId}")
    public ResponseEntity<GrowthChartShare> shareGrowthChart(
            @PathVariable Long fetusId,
            @RequestBody GrowthChartShareRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canShareGrowthChart(userId)) {
            throw new MembershipFeatureException("Tính năng này yêu cầu gói Basic hoặc Premium");
        }
        return ResponseEntity.ok(fetusRecordService.createGrowthChartShare(
            fetusId, 
            request.getChartTypes(), 
            request.getTitle(), 
            request.getContent(),
            userId
        ));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getChartData(@PathVariable Long postId) {
        return ResponseEntity.ok(fetusRecordService.getChartDataForDisplay(postId));
    }
}