
package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.ReminderHealthAlertDTO;
import com.example.pregnancy_tracking.service.ReminderHealthAlertService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
import com.example.pregnancy_tracking.service.MembershipService;
import java.util.List;
import com.example.pregnancy_tracking.entity.User;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/health-alerts")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderHealthAlertController {
    private final ReminderHealthAlertService service;
    private final MembershipService membershipService;

    public ReminderHealthAlertController(ReminderHealthAlertService service, 
                                       MembershipService membershipService) {  // Thay đổi tham số
        this.service = service;
        this.membershipService = membershipService;
    }

    @Operation(summary = "Lấy tất cả cảnh báo sức khỏe", 
              description = "Lấy danh sách tất cả các cảnh báo sức khỏe cho người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách cảnh báo thành công"),
            @ApiResponse(responseCode = "403", description = "Yêu cầu gói Premium"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping
    public ResponseEntity<List<ReminderHealthAlertDTO>> getHealthAlerts(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("Tính năng này yêu cầu gói Premium");
        }
        return ResponseEntity.ok(service.getAllHealthAlerts());
    }

    @Operation(summary = "Lấy cảnh báo sức khỏe theo nhắc nhở", 
              description = "Lấy danh sách cảnh báo sức khỏe cho một nhắc nhở cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách cảnh báo thành công"),
            @ApiResponse(responseCode = "403", description = "Yêu cầu gói Premium"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhắc nhở"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{reminderId}")
    public ResponseEntity<List<ReminderHealthAlertDTO>> getHealthAlertsByReminder(
            @PathVariable Long reminderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("Tính năng này yêu cầu gói Premium");
        }
        return ResponseEntity.ok(service.getHealthAlertsByReminder(reminderId));
    }

    @Operation(summary = "Tạo cảnh báo sức khỏe mới", 
              description = "Tạo một cảnh báo sức khỏe mới cho nhắc nhở.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo cảnh báo thành công"),
            @ApiResponse(responseCode = "403", description = "Yêu cầu gói Premium"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhắc nhở"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/{reminderId}")
    public ResponseEntity<?> createHealthAlert(
            @PathVariable Long reminderId,
            @RequestBody ReminderHealthAlertDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("Tính năng này yêu cầu gói Premium");
        }
        return ResponseEntity.ok(service.createHealthAlert(reminderId, dto));
    }

    @Operation(summary = "Xóa cảnh báo sức khỏe", 
              description = "Xóa một cảnh báo sức khỏe cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa cảnh báo thành công"),
            @ApiResponse(responseCode = "403", description = "Yêu cầu gói Premium"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy cảnh báo"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    
    @DeleteMapping("/{healthAlertId}")
    public ResponseEntity<Void> deleteHealthAlert(
            @PathVariable Long healthAlertId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("Tính năng này yêu cầu gói Premium");
        }
        service.deleteHealthAlert(healthAlertId);
        return ResponseEntity.noContent().build();
    }
}
