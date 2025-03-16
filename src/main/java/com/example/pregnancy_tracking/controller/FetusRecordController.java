package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.FetusRecordDTO;
import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.service.FetusRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.pregnancy_tracking.service.MembershipService;
import com.example.pregnancy_tracking.entity.User;

@RestController
@RequestMapping("/api/fetus-records")
@SecurityRequirement(name = "Bearer Authentication")
public class FetusRecordController {
    @Autowired
    private FetusRecordService fetusRecordService;
    
    @Autowired
    private MembershipService membershipService; // Thay đổi service

    @PostMapping("/{fetusId}")
    public ResponseEntity<FetusRecord> createRecord(
            @PathVariable Long fetusId,
            @RequestBody @Valid FetusRecordDTO recordDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canCreateFetusRecord(userId)) {
            throw new MembershipFeatureException("This feature requires Basic or Premium membership");
        }
        FetusRecord createdRecord = fetusRecordService.createRecord(fetusId, recordDTO, userId);
        return ResponseEntity.ok(createdRecord);
    }

    @GetMapping("/{fetusId}/growth-data")
    public ResponseEntity<Map<String, List<Object[]>>> getAllGrowthData(
            @PathVariable Long fetusId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        Map<String, List<Object[]>> growthData = fetusRecordService.getAllGrowthData(fetusId, userId);
        if (growthData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(growthData);
    }
}

