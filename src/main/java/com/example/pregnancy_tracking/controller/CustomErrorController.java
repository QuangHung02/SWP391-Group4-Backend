package com.example.pregnancy_tracking.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @RequestMapping
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();

        HttpStatus status = getStatus(request);
        String message = getErrorMessage(status);

        errorDetails.put("timestamp", System.currentTimeMillis());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        errorDetails.put("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        return ResponseEntity.status(status).body(errorDetails);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String getErrorMessage(HttpStatus status) {
        switch (status) {
            case NOT_FOUND:
                return "The requested resource was not found";
            case FORBIDDEN:
                return "Access to this resource is forbidden";
            case UNAUTHORIZED:
                return "Authentication is required to access this resource";
            default:
                return "An unexpected error occurred";
        }
    }
}