package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
}