package com.example.pregnancy_tracking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MembershipFeatureException extends RuntimeException {
    private final String requiredPlan;
    private final String redirectUrl;

    public MembershipFeatureException(String message) {
        super(message);
        this.requiredPlan = "Premium";
        this.redirectUrl = "/upgrade-membership";
    }

    public MembershipFeatureException(String message, String requiredPlan) {
        super(message);
        this.requiredPlan = requiredPlan;
        this.redirectUrl = "/upgrade-membership";
    }

    public String getRequiredPlan() {
        return requiredPlan;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}