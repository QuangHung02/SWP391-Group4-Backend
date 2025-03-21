package com.example.pregnancy_tracking.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.pregnancy_tracking.dto.TokenRequest;
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Token", description = "FCM Token management APIs")
public class TokenController {
    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Update FCM token", 
              description = "Updates the Firebase Cloud Messaging token for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token provided"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/fcm-token")
    public ResponseEntity<?> updateFcmToken(@RequestBody TokenRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid FCM token");
            }
            
            user.setFcmToken(request.getToken());
            userRepository.save(user);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating FCM token: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to update FCM token");
        }
    }
}