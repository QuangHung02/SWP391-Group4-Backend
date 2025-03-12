package com.example.pregnancy_tracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;  // Thêm import này

@Data
public class BlogRequest {
    @NotBlank
    private String title;
    
    @NotBlank
    private String content;
    
    private List<String> imageUrls;
}