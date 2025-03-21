package com.example.pregnancy_tracking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HealthAlertException.class)
    public ResponseEntity<Map<String, Object>> handleHealthAlert(HealthAlertException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Health Alert");
        response.put("message", ex.getMessage());
        response.put("healthType", ex.getHealthType());
        response.put("severity", ex.getSeverity());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MembershipFeatureException.class)
    public ResponseEntity<Map<String, Object>> handleMembershipException(MembershipFeatureException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "MEMBERSHIP_REQUIRED");
        response.put("message", ex.getMessage());
        response.put("requiredPlan", ex.getRequiredPlan());
        response.put("redirectUrl", ex.getRedirectUrl());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Resource Not Found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation Error");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Access Denied");
        response.put("message", "Không có quyền truy cập");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
