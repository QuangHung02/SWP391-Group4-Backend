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
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.transaction.annotation.Isolation;

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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubscriptionDTO createSubscription(Long userId, Long packageId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            MembershipPackage newPackage = packageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy gói đăng ký"));
            List<Subscription> existingActive = subscriptionRepository.findByUserIdAndStatus(userId, "Active");
            if (!existingActive.isEmpty()) {
                Subscription currentSub = existingActive.get(0);
                
                if (currentSub.getMembershipPackage().getId().equals(packageId)) {
                    currentSub.setEndDate(currentSub.getEndDate().plusDays(newPackage.getDuration()));
                    currentSub.setCreatedAt(LocalDateTime.now());
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
                    newSubscription.setCreatedAt(LocalDateTime.now());
                    return convertToDTO(subscriptionRepository.save(newSubscription));
                } else {
                    throw new RuntimeException("Không thể đăng ký gói thấp hơn gói hiện tại");
                }
            }

            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setMembershipPackage(newPackage);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusDays(newPackage.getDuration()));
            subscription.setStatus("Active");
            subscription.setCreatedAt(LocalDateTime.now());
            return convertToDTO(subscriptionRepository.save(subscription));
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo đăng ký: " + e.getMessage());
        }
    }

    public Map<String, Object> calculateRevenue() {
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        Map<String, Object> statistics = new HashMap<>();
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        Map<String, BigDecimal> revenueByPackage = new HashMap<>();
        Map<String, Integer> subscriptionsByPackage = new HashMap<>();
        for (Subscription subscription : allSubscriptions) {
            MembershipPackage pack = subscription.getMembershipPackage();
            BigDecimal price = pack.getPrice();
            String packageName = pack.getName();
            totalRevenue = totalRevenue.add(price);
            revenueByPackage.merge(packageName, price, BigDecimal::add);
            subscriptionsByPackage.merge(packageName, 1, Integer::sum);
        }
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("revenueByPackage", revenueByPackage);
        statistics.put("subscriptionsByPackage", subscriptionsByPackage);
        statistics.put("totalSubscriptions", allSubscriptions.size());
        statistics.put("lastUpdated", LocalDateTime.now());
        return statistics;
    }

    @Transactional
    public void handleUserDeletion(Long userId) {
        // Delete active subscriptions
        subscriptionRepository.deleteByUserId(userId);
    }
}