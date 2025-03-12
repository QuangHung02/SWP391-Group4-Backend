package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogImageRepository extends JpaRepository<BlogImage, Long> {
}