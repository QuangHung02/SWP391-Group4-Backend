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

    @Operation(summary = "Get all membership packages", description = "Retrieves a list of all available membership packages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Packages retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/packages")
    public ResponseEntity<List<MembershipPackageDTO>> getAllPackages() {
        return ResponseEntity.ok(membershipService.getAllPackages());
    }

    @Operation(summary = "Get upgrade price", description = "Calculates the upgrade price for a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upgrade price retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/upgrade-price/{userId}")
    public ResponseEntity<MembershipPackageDTO> getUpgradePrice(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.calculateUpgradePrice(userId));
    }

    @Operation(summary = "Upgrade user to premium", description = "Upgrades a user to a premium membership.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User upgraded to premium successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/upgrade/{userId}")
    public ResponseEntity<String> upgradeToPremium(@PathVariable Long userId) {
        membershipService.upgradeToPremium(userId);
        return ResponseEntity.ok("Upgraded to Premium successfully");
    }

    @Operation(summary = "Get user subscription history", description = "Retrieves the subscription history of a specific user. (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription history retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/subscriptions/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @Operation(summary = "Get all subscriptions", description = "Retrieves all user subscriptions. (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All subscriptions retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/subscriptions/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @Operation(summary = "Update membership package", description = "Updates a membership package. (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership package updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
            @ApiResponse(responseCode = "404", description = "Package not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/packages/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPackageDTO> updatePackage(
            @PathVariable Long packageId,
            @Valid @RequestBody MembershipPackageDTO packageDTO) {
        return ResponseEntity.ok(membershipService.updatePackage(packageId, packageDTO));
    }
