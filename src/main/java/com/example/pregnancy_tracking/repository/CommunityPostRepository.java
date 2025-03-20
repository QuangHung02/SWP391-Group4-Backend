package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByAuthorId(Long authorId);
    void deleteByAuthorId(Long authorId);
}