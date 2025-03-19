package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.*;
import com.example.pregnancy_tracking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

// Thêm imports này vào đầu file
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.repository.UserRepository;
import com.example.pregnancy_tracking.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    
    @Operation(summary = "Lấy tất cả người dùng", description = "Lấy danh sách tất cả người dùng đã đăng ký.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập - Chỉ Admin"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Lấy thông tin người dùng theo ID", description = "Lấy chi tiết thông tin người dùng dựa trên ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Cập nhật thông tin người dùng", description = "Cập nhật thông tin người dùng dựa trên ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @Operation(summary = "Đăng ký người dùng mới", description = "Tạo tài khoản người dùng mới trong hệ thống.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đăng ký không hợp lệ"),
            @ApiResponse(responseCode = "409", description = "Email đã được sử dụng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @Operation(summary = "Đăng nhập", description = "Xác thực người dùng và trả về token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @ApiResponse(responseCode = "401", description = "Thông tin đăng nhập không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "Đổi mật khẩu", description = "Cho phép người dùng thay đổi mật khẩu một cách an toàn.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Mật khẩu không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Chưa xác thực: Không có thông tin xác thực.");
        }
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }

    @Operation(summary = "Xóa người dùng", description = "Xóa tài khoản người dùng khỏi hệ thống (Chỉ dành cho Admin).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa người dùng thành công"),
            @ApiResponse(responseCode = "403", description = "Từ chối truy cập - Chỉ dành cho Admin"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa người dùng thành công");
    }

    @PutMapping("/{userId}/fcm-token")
    public ResponseEntity<?> updateFCMToken(@PathVariable Long userId, @RequestBody FCMTokenDTO tokenDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFcmToken(tokenDTO.getFcmToken());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
