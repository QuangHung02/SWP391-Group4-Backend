package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.MembershipPackageDTO;
import com.example.pregnancy_tracking.dto.SubscriptionDTO;
import com.example.pregnancy_tracking.service.MembershipService;
import com.example.pregnancy_tracking.service.SubscriptionService;
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

    @GetMapping("/packages")
    public ResponseEntity<List<MembershipPackageDTO>> getAllPackages() {
        return ResponseEntity.ok(membershipService.getAllPackages());
    }


    @GetMapping("/upgrade-price/{userId}")
    public ResponseEntity<MembershipPackageDTO> getUpgradePrice(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.calculateUpgradePrice(userId));
    }

    @PostMapping("/upgrade/{userId}")
    public ResponseEntity<String> upgradeToPremium(@PathVariable Long userId) {
        membershipService.upgradeToPremium(userId);
        return ResponseEntity.ok("Upgraded to Premium successfully");
    }

    @GetMapping("/subscriptions/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @GetMapping("/subscriptions/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }
}