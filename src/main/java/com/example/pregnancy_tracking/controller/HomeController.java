package com.example.pregnancy_tracking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Operation(summary = "Get API Home", description = "Returns the status and basic information about the Pregnancy Tracking API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API is running"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "running");
        response.put("service", "Pregnancy Tracking API");
        response.put("version", "1.0");
        response.put("endpoints", new String[]{
                "/api/auth/register",
                "/api/auth/login",
                "/api/users",
                "/api/users/{id}",
                "/api/users/profile",
                "/api/users/change-password",
                "/api/membership/packages",
                "/api/membership/upgrade"
        });

        return ResponseEntity.ok(response);
    }
}
