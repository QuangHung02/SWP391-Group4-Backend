package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdOrderByStartDateDesc(Long userId);

    Optional<Subscription> findFirstByUserIdAndStatusOrderByEndDateDesc(Long userId, String status);

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

    boolean existsByUserIdAndCreatedAtAfter(Long userId, LocalDateTime dateTime);

    @Query("SELECT COUNT(s) > 0 FROM Subscription s " +
            "WHERE s.user.id = :userId " +
            "AND s.membershipPackage.id = :packageId " +
            "AND s.createdAt > :dateTime")
    boolean existsByUserIdAndPackageIdAndCreatedAtAfter(
            @Param("userId") Long userId,
            @Param("packageId") Long packageId,
            @Param("dateTime") LocalDateTime dateTime);

    void deleteByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = :status")
    List<Subscription> findByUserIdAndStatusForUpdate(Long userId, String status);

    List<Subscription> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime dateTime);
}