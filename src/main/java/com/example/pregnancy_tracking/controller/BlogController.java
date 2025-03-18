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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> createBlog(
            @Valid @RequestBody BlogRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(blogService.createBlog(request, userEmail));
    }

    @Operation(summary = "Cập nhật bài viết", description = "Admin có thể cập nhật bài viết đã tồn tại.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật bài viết thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PutMapping("/{blogId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> updateBlog(
            @PathVariable Long blogId,
            @Valid @RequestBody BlogRequest request) {
        return ResponseEntity.ok(blogService.updateBlog(blogId, request));
    }

    @Operation(summary = "Xóa bài viết", description = "Admin có thể xóa bài viết.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa bài viết thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @DeleteMapping("/{blogId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBlog(@PathVariable Long blogId) {
        blogService.deleteBlog(blogId);
        return ResponseEntity.ok("Xóa bài viết thành công");
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả bài viết", description = "API công khai để lấy tất cả bài viết.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách bài viết thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @GetMapping("/{blogId}")
    @Operation(summary = "Lấy bài viết theo ID", description = "API công khai để lấy một bài viết cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy bài viết thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bài viết"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    public ResponseEntity<Blog> getBlogById(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.getBlogById(blogId));
    }

    @GetMapping("/featured")
    @Operation(summary = "Lấy bài viết nổi bật", description = "API công khai để lấy các bài viết nổi bật cho trang chủ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy bài viết nổi bật thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    public ResponseEntity<List<Blog>> getFeaturedBlogs() {
        return ResponseEntity.ok(blogService.getFeaturedBlogs());
    }
}
