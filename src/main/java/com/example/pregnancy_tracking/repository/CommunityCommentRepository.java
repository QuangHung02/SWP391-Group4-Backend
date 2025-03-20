package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByAuthorId(Long authorId);
    void deleteByAuthorId(Long authorId);
}