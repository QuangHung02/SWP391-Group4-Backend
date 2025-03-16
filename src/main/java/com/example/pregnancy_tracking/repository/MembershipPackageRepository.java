package com.example.pregnancy_tracking.repository;

import com.example.pregnancy_tracking.entity.MembershipPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPackageRepository extends JpaRepository<MembershipPackage, Long> {
    MembershipPackage findByName(String name);
}