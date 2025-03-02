package com.example.pregnancy_tracking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "running");
        response.put("service", "Pregnancy Tracking API");
        response.put("version", "1.0");
        response.put("endpoints", new String[]{
            "/api/user/register",
            "/api/user/login",
            "/api/user/all",
            "/api/user/{id}",
            "/api/user/change-password"
        });
        
        return ResponseEntity.ok(response);
    }
}