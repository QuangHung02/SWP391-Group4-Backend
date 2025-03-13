package com.example.pregnancy_tracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private List<String> imageUrls;
}