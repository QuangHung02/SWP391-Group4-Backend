package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Tạo URL thanh toán", 
              description = "Tạo đường dẫn thanh toán VNPay cho gói thành viên đã chọn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo URL thanh toán thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng hoặc gói thành viên"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/create/{userId}/{packageId}")
    public ResponseEntity<String> createPayment(
            @PathVariable Long userId,
            @PathVariable Long packageId,
            @RequestParam String returnUrl) {
        String paymentUrl = paymentService.createPaymentUrl(userId, packageId, returnUrl);
        return ResponseEntity.ok(paymentUrl);
    }

    @Operation(summary = "Xử lý kết quả thanh toán", 
              description = "Xử lý phản hồi từ cổng thanh toán VNPay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xử lý thanh toán thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc thanh toán thất bại"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/vnpay-return")
    public ResponseEntity<String> paymentReturn(@RequestParam Map<String, String> queryParams) {
        paymentService.processPaymentReturn(queryParams);
        return ResponseEntity.ok("Thanh toán thành công!");
    }
}