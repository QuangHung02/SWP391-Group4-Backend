package com.example.pregnancy_tracking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health/database")
    public ResponseEntity<?> checkDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection
            dataSource.getConnection();
            
            // Execute a simple query
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            response.put("status", "UP");
            response.put("database", "Connected");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("database", "Disconnected");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}