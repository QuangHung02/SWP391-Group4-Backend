package com.example.pregnancy_tracking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Operation(summary = "Check Database Connection", description = "Checks if the application can successfully connect to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Database is connected"),
            @ApiResponse(responseCode = "500", description = "Database is not reachable")
    })
    @GetMapping("/database")
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
