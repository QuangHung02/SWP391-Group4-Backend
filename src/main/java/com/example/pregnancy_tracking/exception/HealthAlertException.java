package com.example.pregnancy_tracking.exception;

import com.example.pregnancy_tracking.entity.HealthType;
import com.example.pregnancy_tracking.entity.SeverityLevel;

public class HealthAlertException extends RuntimeException {
    private final String message;
    private final HealthType healthType;
    private final SeverityLevel severity;

    public HealthAlertException(String message, HealthType healthType, SeverityLevel severity) {
        super(message);
        this.message = message;
        this.healthType = healthType;
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public HealthType getHealthType() {
        return healthType;
    }

    public SeverityLevel getSeverity() {
        return severity;
    }
}