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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
    @Operation(summary = "Lấy các loại đồ thị khả dụng", 
              description = "Lấy danh sách các loại đồ thị có thể xem với thai nhi được chọn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thai nhi"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{fetusId}/available-charts")
    public ResponseEntity<Map<ChartType, Boolean>> getAvailableCharts(
            @PathVariable Long fetusId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        return ResponseEntity.ok(fetusRecordService.getAvailableChartTypes(fetusId, userId));
    }

    @Operation(summary = "Chia sẻ đồ thị tăng trưởng", 
              description = "Tạo bài đăng chia sẻ đồ thị tăng trưởng của thai nhi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chia sẻ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "403", description = "Không có quyền - Yêu cầu gói Basic hoặc Premium"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thai nhi"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
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
            userId,
            request.getIsAnonymous()
        ));
    }
}