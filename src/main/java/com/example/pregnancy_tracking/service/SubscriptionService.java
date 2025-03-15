package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.MembershipPackage;
import com.example.pregnancy_tracking.entity.Subscription;
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.repository.MembershipPackageRepository;
import com.example.pregnancy_tracking.repository.SubscriptionRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.pregnancy_tracking.dto.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MembershipPackageRepository packageRepository;

    private SubscriptionDTO convertToDTO(Subscription subscription) {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUser().getId());
        dto.setUsername(subscription.getUser().getUsername());
        dto.setPackageId(subscription.getMembershipPackage().getId());
        dto.setPackageName(subscription.getMembershipPackage().getName());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setStatus(subscription.getStatus());
        dto.setCreatedAt(subscription.getCreatedAt());
        return dto;
    }

    public List<SubscriptionDTO> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SubscriptionDTO> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubscriptionDTO createSubscription(Long userId, Long packageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MembershipPackage newPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        Optional<Subscription> activeSubscription = subscriptionRepository
                .findFirstByUserIdAndStatusOrderByEndDateDesc(userId, "Active");

        if (activeSubscription.isPresent()) {
            Subscription currentSub = activeSubscription.get();

            if (currentSub.getMembershipPackage().getId().equals(packageId)) {
                currentSub.setEndDate(currentSub.getEndDate().plusDays(newPackage.getDuration()));
                return convertToDTO(subscriptionRepository.save(currentSub));
            }

            if (newPackage.getPrice().compareTo(currentSub.getMembershipPackage().getPrice()) > 0) {
                currentSub.setStatus("Expired");
                currentSub.setEndDate(LocalDate.now());
                subscriptionRepository.save(currentSub);

                Subscription newSubscription = new Subscription();
                newSubscription.setUser(user);
                newSubscription.setMembershipPackage(newPackage);
                newSubscription.setStartDate(LocalDate.now());
                newSubscription.setEndDate(LocalDate.now().plusDays(newPackage.getDuration()));
                newSubscription.setStatus("Active");

                return convertToDTO(subscriptionRepository.save(newSubscription));
            }
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setMembershipPackage(newPackage);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(newPackage.getDuration()));
        subscription.setStatus("Active");

        return convertToDTO(subscriptionRepository.save(subscription));
    }

}