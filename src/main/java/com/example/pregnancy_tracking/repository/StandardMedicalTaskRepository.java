package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.StandardMedicalTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandardMedicalTaskRepository extends JpaRepository<StandardMedicalTask, Long> {
    List<StandardMedicalTask> findByWeek(Integer week);
    List<StandardMedicalTask> findAllByOrderByWeekAsc();
}