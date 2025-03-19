package com.example.pregnancy_tracking.controller;

import jakarta.validation.Valid;
import com.example.pregnancy_tracking.dto.MembershipPackageDTO;
import com.example.pregnancy_tracking.dto.SubscriptionDTO;
import com.example.pregnancy_tracking.service.MembershipService;
import com.example.pregnancy_tracking.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {
    private final MembershipService membershipService;
    private final SubscriptionService subscriptionService;

    @Operation(summary = "Lấy tất cả gói thành viên", description = "Lấy danh sách tất cả các gói thành viên hiện có.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách gói thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/packages")
    public ResponseEntity<List<MembershipPackageDTO>> getAllPackages() {
        return ResponseEntity.ok(membershipService.getAllPackages());
    }

    @Operation(summary = "Lấy giá nâng cấp", description = "Tính toán giá nâng cấp cho người dùng cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy giá nâng cấp thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/upgrade-price/{userId}")
    public ResponseEntity<MembershipPackageDTO> getUpgradePrice(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.calculateUpgradePrice(userId));
    }

    @Operation(summary = "Nâng cấp lên gói Premium", description = "Nâng cấp người dùng lên gói thành viên Premium.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nâng cấp lên Premium thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/upgrade/{userId}")
    public ResponseEntity<String> upgradeToPremium(@PathVariable Long userId) {
        membershipService.upgradeToPremium(userId);
        return ResponseEntity.ok("Nâng cấp lên Premium thành công");
    }

    @Operation(summary = "Xem lịch sử đăng ký", description = "Lấy lịch sử đăng ký của người dùng cụ thể. (Chỉ Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy lịch sử đăng ký thành công"),
            @ApiResponse(responseCode = "403", description = "Từ chối truy cập - Chỉ dành cho Admin"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/subscriptions/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @Operation(summary = "Xem tất cả đăng ký", description = "Lấy tất cả đăng ký của người dùng. (Chỉ Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy tất cả đăng ký thành công"),
            @ApiResponse(responseCode = "403", description = "Từ chối truy cập - Chỉ dành cho Admin"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/subscriptions/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @Operation(summary = "Cập nhật gói thành viên", description = "Cập nhật thông tin gói thành viên. (Chỉ Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật gói thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "403", description = "Từ chối truy cập - Chỉ dành cho Admin"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy gói"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PutMapping("/packages/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPackageDTO> updatePackage(
            @PathVariable Long packageId,
            @Valid @RequestBody MembershipPackageDTO packageDTO) {
        return ResponseEntity.ok(membershipService.updatePackage(packageId, packageDTO));
    }
}