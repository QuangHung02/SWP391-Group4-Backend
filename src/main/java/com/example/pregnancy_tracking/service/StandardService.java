package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.PregnancyStandardDTO;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.entity.MomStandard;
import com.example.pregnancy_tracking.entity.PregnancyStandardId;
import com.example.pregnancy_tracking.repository.PregnancyStandardRepository;
import com.example.pregnancy_tracking.repository.MomStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StandardService {
    @Autowired
    private PregnancyStandardRepository pregnancyStandardRepository;

    @Autowired
    private MomStandardRepository momStandardRepository;

    public List<PregnancyStandardDTO> getAllPregnancyStandards() {
        List<PregnancyStandard> standards = pregnancyStandardRepository.findAllByOrderByIdWeekAsc();
        return standards.stream().map(PregnancyStandardDTO::new).collect(Collectors.toList());
    }

    public Optional<MomStandard> getMomStandard(Integer week) {
        return momStandardRepository.findByWeek(week);
    }
}
