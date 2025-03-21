package com.example.pregnancy_tracking.entity;

public enum NotificationType {
    MEDICAL_TASK("medical_task"),
    HEALTH_ALERT("health_alert");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
