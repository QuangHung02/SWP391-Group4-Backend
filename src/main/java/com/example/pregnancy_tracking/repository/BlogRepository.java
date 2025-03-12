package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}