package com.example.pregnancy_tracking.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "FCM Token request object")
public class TokenRequest {
    @Schema(description = "Firebase Cloud Messaging token", example = "fMQE8SyWSh-jUJxGvL...")
    private String token;
}