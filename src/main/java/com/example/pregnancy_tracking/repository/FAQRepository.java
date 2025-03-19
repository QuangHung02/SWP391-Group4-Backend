package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
}