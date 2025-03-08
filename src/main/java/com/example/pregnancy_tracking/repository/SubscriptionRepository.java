package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // Tìm subscription active mới nhất của user
    Optional<Subscription> findFirstByUserIdAndStatusOrderByEndDateDesc(Long userId, String status);
    
    // Lấy lịch sử subscription của user
    List<Subscription> findByUserIdOrderByStartDateDesc(Long userId);
    
    // Kiểm tra subscription active của user với package cụ thể
    @Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.user.id = :userId " +
           "AND s.membershipPackage.id = :packageId AND s.status = :status")
    boolean existsByUserIdAndPackageIdAndStatus(
        @Param("userId") Long userId, 
        @Param("packageId") Long packageId, 
        @Param("status") String status
    );
    
    // Tìm subscription active của user với package cụ thể
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId " +
           "AND s.membershipPackage.id = :packageId AND s.status = :status")
    Optional<Subscription> findByUserIdAndPackageIdAndStatus(
        @Param("userId") Long userId, 
        @Param("packageId") Long packageId, 
        @Param("status") String status
    );
}
