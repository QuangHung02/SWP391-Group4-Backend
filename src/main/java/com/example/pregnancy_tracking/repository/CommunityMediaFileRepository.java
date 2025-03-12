package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.CommunityMediaFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityMediaFileRepository extends JpaRepository<CommunityMediaFiles, Long> {
}