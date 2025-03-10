package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
}