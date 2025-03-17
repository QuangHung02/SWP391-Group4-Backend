package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.GrowthChartShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface GrowthChartShareRepository extends JpaRepository<GrowthChartShare, Long> {
    List<GrowthChartShare> findByFetusFetusId(Long fetusId);
    @Query("SELECT g FROM GrowthChartShare g LEFT JOIN FETCH g.post p LEFT JOIN FETCH p.author WHERE g.post.postId = :postId")
Optional<GrowthChartShare> findByPostPostId(Long postId);
}