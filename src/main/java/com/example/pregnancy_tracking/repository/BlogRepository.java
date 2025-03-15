package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findTop5ByOrderByCreatedAtDesc();
}