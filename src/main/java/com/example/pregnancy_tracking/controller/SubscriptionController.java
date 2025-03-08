package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.SubscriptionService;
import com.example.pregnancy_tracking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.pregnancy_tracking.dto.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

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
}