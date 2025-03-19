package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.SubscriptionService;
import com.example.pregnancy_tracking.security.JwtUtil;
import com.example.pregnancy_tracking.dto.SubscriptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Đăng ký gói thành viên", 
              description = "Đăng ký gói thành viên cho người dùng đã xác thực.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng ký gói thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực - Token không hợp lệ hoặc thiếu"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/subscribe/{packageId}")
    public ResponseEntity<SubscriptionDTO> subscribe(
            @PathVariable Long packageId,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing token");
        }

        String token = authHeader.substring(7);
        try {
            Long userId = jwtUtil.extractUserId(token);
            SubscriptionDTO subscription = subscriptionService.createSubscription(userId, packageId);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            throw new RuntimeException("Error processing token: " + e.getMessage());
        }
    }

    @Operation(summary = "Lấy danh sách đăng ký của người dùng", 
              description = "Lấy tất cả đăng ký của người dùng đã xác thực.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách đăng ký thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực - Token không hợp lệ hoặc thiếu"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/my-subscriptions")
    public ResponseEntity<List<SubscriptionDTO>> getMySubscriptions(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing token");
        }

        String token = authHeader.substring(7);
        try {
            Long userId = jwtUtil.extractUserId(token);
            List<SubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            throw new RuntimeException("Error processing token: " + e.getMessage());
        }
    }

    @Operation(summary = "Lấy thống kê doanh thu", 
              description = "Lấy thống kê doanh thu từ tất cả đăng ký. Chỉ dành cho Admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thống kê thành công"),
            @ApiResponse(responseCode = "403", description = "Từ chối truy cập - Không phải Admin"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/revenue-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics() {
        Map<String, Object> statistics = subscriptionService.calculateRevenue();
        return ResponseEntity.ok(statistics);
    }
}
