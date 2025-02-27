package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}
