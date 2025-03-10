package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.entity.Pregnancy;
import com.example.pregnancy_tracking.entity.PregnancyStatus;
import com.example.pregnancy_tracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PregnancyRepository extends JpaRepository<Pregnancy, Long> {
    List<Pregnancy> findByUser(User user);
    boolean existsByUserIdAndStatus(Long userId, PregnancyStatus status);
}