package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.BlogRequest;
import com.example.pregnancy_tracking.entity.Blog;
import com.example.pregnancy_tracking.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BlogController {
    private final BlogService blogService;

    @Operation(summary = "Create a new blog", description = "Admins can create a new blog post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> createBlog(
            @Valid @RequestBody BlogRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(blogService.createBlog(request, userEmail));
    }

    @Operation(summary = "Update a blog", description = "Admins can update an existing blog post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Blog not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{blogId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> updateBlog(
            @PathVariable Long blogId,
            @Valid @RequestBody BlogRequest request) {
        return ResponseEntity.ok(blogService.updateBlog(blogId, request));
    }

    @Operation(summary = "Delete a blog", description = "Admins can delete a blog post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Blog not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/{blogId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBlog(@PathVariable Long blogId) {
        blogService.deleteBlog(blogId);
        return ResponseEntity.ok("Blog deleted successfully");
    }

    @Operation(summary = "Get all blogs", description = "Retrieves a list of all blog posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blogs retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @Operation(summary = "Get a blog by ID", description = "Retrieves details of a specific blog post by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{blogId}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.getBlogById(blogId));
    }
}
