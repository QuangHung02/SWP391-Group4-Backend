package com.example.pregnancy_tracking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MembershipFeatureException extends RuntimeException {
    public MembershipFeatureException(String message) {
        super(message);
    }
}