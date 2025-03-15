package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.GrowthChartShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GrowthChartShareRepository extends JpaRepository<GrowthChartShare, Long> {
    Optional<GrowthChartShare> findByPostPostId(Long postId);
    List<GrowthChartShare> findByFetusFetusId(Long fetusId);
}