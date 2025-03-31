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
    private MembershipService membershipService;

    @Operation(summary = "Tạo bản ghi theo dõi thai nhi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo bản ghi thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Yêu cầu gói thành viên"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/{fetusId}")
    public ResponseEntity<FetusRecord> createRecord(
            @PathVariable Long fetusId,
            @RequestBody @Valid FetusRecordDTO recordDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canCreateFetusRecord(userId)) {
            throw new MembershipFeatureException("Tính năng này yêu cầu gói Basic hoặc Premium");
        }
        FetusRecord createdRecord = fetusRecordService.createRecord(fetusId, recordDTO, userId);
        return ResponseEntity.ok(createdRecord);
    }

    @Operation(summary = "Lấy danh sách bản ghi theo ID thai nhi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách bản ghi thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bản ghi"),
            @ApiResponse(responseCode = "403", description = "Yêu cầu gói thành viên"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{fetusId}")
    public ResponseEntity<List<FetusRecordDTO>> getRecordsByFetusId(
            @PathVariable Long fetusId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        List<FetusRecordDTO> records = fetusRecordService.getRecordsByFetusId(fetusId, userId);
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "Lấy tất cả dữ liệu đo lường sự phát triển")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy dữ liệu đo lường thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy dữ liệu đo lường"),
            @ApiResponse(responseCode = "403", description = "Yêu cầu gói thành viên"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{fetusId}/growth-data")
    public ResponseEntity<Map<String, List<Object[]>>> getAllGrowthData(
            @PathVariable Long fetusId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        Map<String, List<Object[]>> predictionData = fetusRecordService.getPredictionData(fetusId, userId);
        if (predictionData.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(predictionData);
    }
}

