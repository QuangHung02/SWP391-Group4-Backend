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

    @Operation(summary = "Subscribe to a package", description = "Subscribes the authenticated user to a membership package.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "500", description = "Server error")
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

    @Operation(summary = "Get user subscriptions", description = "Retrieves all subscriptions for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "500", description = "Server error")
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

    @Operation(summary = "Get revenue statistics", description = "Retrieves revenue statistics for all subscriptions. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not an admin"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/revenue-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics() {
        Map<String, Object> statistics = subscriptionService.calculateRevenue();
        return ResponseEntity.ok(statistics);
    }
}
