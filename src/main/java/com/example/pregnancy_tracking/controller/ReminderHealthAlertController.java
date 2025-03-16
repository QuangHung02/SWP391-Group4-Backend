
package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.ReminderHealthAlertDTO;
import com.example.pregnancy_tracking.service.ReminderHealthAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
import com.example.pregnancy_tracking.service.MembershipService;
import java.util.List;
import com.example.pregnancy_tracking.entity.User;

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

    @GetMapping
    public ResponseEntity<List<ReminderHealthAlertDTO>> getHealthAlerts(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("This feature requires Premium membership");
        }
        return ResponseEntity.ok(service.getAllHealthAlerts());
    }

    @GetMapping("/{reminderId}")
    public ResponseEntity<List<ReminderHealthAlertDTO>> getHealthAlertsByReminder(
            @PathVariable Long reminderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("This feature requires Premium membership");
        }
        return ResponseEntity.ok(service.getHealthAlertsByReminder(reminderId));
    }

    @PostMapping("/{reminderId}")
    public ResponseEntity<?> createHealthAlert(
            @PathVariable Long reminderId,
            @RequestBody ReminderHealthAlertDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("This feature requires Premium membership");
        }
        return ResponseEntity.ok(service.createHealthAlert(reminderId, dto));
    }

    @DeleteMapping("/{healthAlertId}")
    public ResponseEntity<Void> deleteHealthAlert(
            @PathVariable Long healthAlertId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        Long userId = user.getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            throw new MembershipFeatureException("This feature requires Premium membership");
        }
        service.deleteHealthAlert(healthAlertId);
        return ResponseEntity.noContent().build();
    }
}
