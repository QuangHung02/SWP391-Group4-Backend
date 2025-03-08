package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.Pregnancy;
import com.example.pregnancy_tracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PregnancyRepository extends JpaRepository<Pregnancy, Long> {
    List<Pregnancy> findByUser(User user);
}
