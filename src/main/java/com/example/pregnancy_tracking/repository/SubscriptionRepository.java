package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdOrderByStartDateDesc(Long userId);
    
    Optional<Subscription> findFirstByUserIdAndStatusOrderByEndDateDesc(Long userId, String status);
    
    @Query("SELECT s FROM Subscription s " +
           "WHERE s.user.id = :userId " +
           "AND s.status = 'Active' " +
           "AND NOT EXISTS (SELECT 1 FROM Subscription s2 " +
           "               WHERE s2.user.id = s.user.id " +
           "               AND s2.id > s.id " +
           "               AND s2.status = 'Active')")
    Optional<Subscription> findLatestActiveSubscription(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(s) > 0 FROM Subscription s " +
           "WHERE s.user.id = :userId " +
           "AND s.membershipPackage.id = :packageId " +
           "AND s.status = :status")
    boolean existsByUserIdAndPackageIdAndStatus(
            @Param("userId") Long userId, 
            @Param("packageId") Long packageId, 
            @Param("status") String status);

    @Query("SELECT s FROM Subscription s " +
           "WHERE s.user.id = :userId " +
           "AND s.endDate >= CURRENT_DATE " +
           "AND s.status = 'Active' " +
           "ORDER BY s.endDate DESC")
    Optional<Subscription> findActiveSubscriptionByUserId(@Param("userId") Long userId);
    List<Subscription> findByUserIdAndStatus(Long userId, String status);
}
