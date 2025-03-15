package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reminder r " +
           "JOIN r.medicalTasks t " +
           "WHERE r.user.id = :userId " +
           "AND r.pregnancy.pregnancyId = :pregnancyId " +
           "AND t.week = :week")
    boolean existsByUserIdAndPregnancyIdAndWeek(
        @Param("userId") Long userId, 
        @Param("pregnancyId") Long pregnancyId, 
        @Param("week") Integer week
    );

    List<Reminder> findByPregnancy_PregnancyId(Long pregnancyId);
}