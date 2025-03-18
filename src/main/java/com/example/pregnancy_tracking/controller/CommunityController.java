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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommunityController {
    private final CommunityService communityService;

    @Operation(summary = "Tạo bài viết", description = "Tạo một bài viết mới trong cộng đồng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo bài viết thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/posts")
    public ResponseEntity<CommunityPost> createPost(
            @Valid @RequestBody PostRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(communityService.createPost(request, userEmail));
    }

    @Operation(summary = "Tạo bình luận", description = "Tạo bình luận mới cho bài viết.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo bình luận thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommunityComment> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(communityService.createComment(postId, request, userEmail));
    }

    @Operation(summary = "Xóa bài viết", description = "Xóa bài viết và tất cả bình luận (Chỉ Admin).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa bài viết thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập - Chỉ dành cho Admin"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        communityService.deletePost(postId);
        return ResponseEntity.ok("Xóa bài viết thành công");
    }

    @Operation(summary = "Xóa bình luận", description = "Xóa bình luận (Chỉ Admin).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa bình luận thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập - Chỉ dành cho Admin"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bình luận"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        communityService.deleteComment(commentId);
        return ResponseEntity.ok("Xóa bình luận thành công");
    }

    @Operation(summary = "Lấy tất cả bài viết", description = "Lấy tất cả bài viết trong cộng đồng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách bài viết thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/posts")
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        return ResponseEntity.ok(communityService.getAllPostsWithCharts());
    }

    @Operation(summary = "Lấy bài viết theo ID", description = "Lấy chi tiết của một bài viết cụ thể theo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy bài viết thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommunityPost> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPostById(postId));
    }

    @Operation(summary = "Lấy bình luận của bài viết", description = "Lấy tất cả bình luận của một bài viết cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách bình luận thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommunityComment>> getPostComments(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getCommentsByPostId(postId));
    }
}
