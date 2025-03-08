package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.MembershipPackageDTO;
import com.example.pregnancy_tracking.entity.MembershipPackage;
import com.example.pregnancy_tracking.repository.MembershipPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipPackageService {
    private final MembershipPackageRepository membershipPackageRepository;

    public List<MembershipPackageDTO> getAllPackages() {
        return membershipPackageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MembershipPackageDTO convertToDTO(MembershipPackage pkg) {
        MembershipPackageDTO dto = new MembershipPackageDTO();
        dto.setId(pkg.getId());
        dto.setName(pkg.getName());
        dto.setDescription(pkg.getDescription());
        dto.setPrice(pkg.getPrice());
        dto.setDuration(pkg.getDuration());
        return dto;
    }
}