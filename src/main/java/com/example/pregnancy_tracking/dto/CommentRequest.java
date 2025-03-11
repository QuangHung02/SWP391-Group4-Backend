package com.example.pregnancy_tracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @NotBlank
    private String content;
    
    private List<String> mediaUrls;
    
    private Boolean isAnonymous = false; 
}