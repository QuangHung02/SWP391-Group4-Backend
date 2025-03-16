package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create/{userId}/{packageId}")
    public ResponseEntity<String> createPayment(
            @PathVariable Long userId,
            @PathVariable Long packageId,
            @RequestParam String returnUrl) {
        String paymentUrl = paymentService.createPaymentUrl(userId, packageId, returnUrl);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> paymentReturn(@RequestParam Map<String, String> queryParams) {
        paymentService.processPaymentReturn(queryParams);
        return ResponseEntity.ok("Payment processed successfully");
    }
}