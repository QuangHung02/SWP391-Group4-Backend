package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.CommentRequest;
import com.example.pregnancy_tracking.dto.PostRequest;
import com.example.pregnancy_tracking.entity.CommunityComment;
import com.example.pregnancy_tracking.entity.CommunityPost;
import com.example.pregnancy_tracking.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/posts")
    public ResponseEntity<CommunityPost> createPost(
            @Valid @RequestBody PostRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(communityService.createPost(request, userEmail));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommunityComment> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(communityService.createComment(postId, request, userEmail));
    }

    @Operation(summary = "Delete post", description = "Deletes a post and all its comments (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        communityService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @Operation(summary = "Delete comment", description = "Deletes a comment (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        communityService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}