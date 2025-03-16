package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.MembershipPackageDTO;
import com.example.pregnancy_tracking.entity.MembershipPackage;
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.repository.MembershipPackageRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.pregnancy_tracking.entity.Subscription;
import com.example.pregnancy_tracking.repository.SubscriptionRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service("membershipService")
@Transactional
@RequiredArgsConstructor
public class MembershipService {
    private final UserRepository userRepository;
    private final MembershipPackageRepository packageRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<MembershipPackageDTO> getAllPackages() {
        return packageRepository.findAll().stream()
                .map(pack -> {
                    MembershipPackageDTO dto = new MembershipPackageDTO();
                    dto.setId(pack.getId());
                    dto.setName(pack.getName());
                    dto.setDescription(pack.getDescription());
                    dto.setPrice(pack.getPrice());
                    dto.setDuration(pack.getDuration());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public MembershipPackageDTO calculateUpgradePrice(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MembershipPackage basicPackage = packageRepository.findByName("Basic Plan");
        MembershipPackage premiumPackage = packageRepository.findByName("Premium Plan");
        if (premiumPackage == null || basicPackage == null) {
            throw new RuntimeException("Package not found");
        }

        MembershipPackageDTO packageDTO = new MembershipPackageDTO();
        packageDTO.setId(premiumPackage.getId());
        packageDTO.setName(premiumPackage.getName());
        packageDTO.setDescription(premiumPackage.getDescription());
        packageDTO.setDuration(premiumPackage.getDuration());

        boolean hasActiveBasic = subscriptionRepository
                .existsByUserIdAndPackageIdAndStatus(userId, basicPackage.getId(), "Active");

        if (hasActiveBasic) {
            packageDTO.setPrice(premiumPackage.getPrice().divide(BigDecimal.valueOf(2)));
        } else {
            packageDTO.setPrice(premiumPackage.getPrice());
        }

        return packageDTO;
    }

    @Transactional
    public void upgradeToPremium(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MembershipPackage basicPackage = packageRepository.findByName("Basic Plan");
        MembershipPackage premiumPackage = packageRepository.findByName("Premium Plan");
        if (premiumPackage == null) {
            throw new RuntimeException("Premium package not found");
        }

        BigDecimal finalPrice;
        boolean hasActiveBasic = subscriptionRepository
                .existsByUserIdAndPackageIdAndStatus(userId, basicPackage.getId(), "Active");
        if (hasActiveBasic) {
            finalPrice = premiumPackage.getPrice().divide(BigDecimal.valueOf(2));
            subscriptionRepository.findFirstByUserIdAndStatusOrderByEndDateDesc(userId, "Active")
                    .ifPresent(sub -> {
                        sub.setStatus("Expired");
                        subscriptionRepository.save(sub);
                    });
        } else {
            finalPrice = premiumPackage.getPrice();
        }

        Subscription premiumSub = new Subscription();
        premiumSub.setUser(user);
        premiumSub.setMembershipPackage(premiumPackage);
        premiumSub.setStartDate(LocalDate.now());
        premiumSub.setEndDate(LocalDate.now().plusDays(premiumPackage.getDuration()));
        premiumSub.setStatus("Active");

        subscriptionRepository.save(premiumSub);
    }

    public MembershipPackageDTO updatePackage(Long packageId, MembershipPackageDTO packageDTO) {
        MembershipPackage existingPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        if (packageDTO.getPrice() != null) {
            existingPackage.setPrice(packageDTO.getPrice());
        }
        if (packageDTO.getName() != null) {
            existingPackage.setName(packageDTO.getName());
        }
        if (packageDTO.getDescription() != null) {
            existingPackage.setDescription(packageDTO.getDescription());
        }
        if (packageDTO.getDuration() != null) {
            existingPackage.setDuration(packageDTO.getDuration());
        }
        MembershipPackage updatedPackage = packageRepository.save(existingPackage);
        MembershipPackageDTO dto = new MembershipPackageDTO();
        dto.setId(updatedPackage.getId());
        dto.setName(updatedPackage.getName());
        dto.setDescription(updatedPackage.getDescription());
        dto.setPrice(updatedPackage.getPrice());
        dto.setDuration(updatedPackage.getDuration());
        dto.setCreatedAt(updatedPackage.getCreatedAt());
        dto.setUpdatedAt(updatedPackage.getUpdatedAt());
        
        return dto;
    }

    public boolean canAccessFeature(Long userId, String packageName) {
        return subscriptionRepository.findActiveSubscriptionByUserId(userId)
            .map(subscription -> packageName.equals(subscription.getMembershipPackage().getName()))
            .orElse(false);
    }

    public boolean canAccessStandardFeatures(Long userId) {
        return true;
    }

    public boolean canCreatePregnancyRecord(Long userId) {
        return canAccessFeature(userId, "Basic Plan") || canAccessFeature(userId, "Premium Plan");
    }

    public boolean canCreateFetusRecord(Long userId) {
        return canAccessFeature(userId, "Basic Plan") || canAccessFeature(userId, "Premium Plan");
    }

    public boolean canShareGrowthChart(Long userId) {
        return canAccessFeature(userId, "Basic Plan") || canAccessFeature(userId, "Premium Plan");
    }

    public boolean canAccessHealthAlerts(Long userId) {
        return canAccessFeature(userId, "Premium Plan");
    }

    public boolean canViewPredictionLine(Long userId) {
        return canAccessFeature(userId, "Premium Plan");
    }
}