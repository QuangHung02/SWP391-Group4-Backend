package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
}