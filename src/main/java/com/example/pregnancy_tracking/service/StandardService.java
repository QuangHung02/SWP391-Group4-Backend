package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.entity.MomStandard;
import com.example.pregnancy_tracking.repository.PregnancyStandardRepository;
import com.example.pregnancy_tracking.repository.MomStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StandardService {
    @Autowired
    private PregnancyStandardRepository pregnancyStandardRepository;

    @Autowired
    private MomStandardRepository momStandardRepository;

    public Optional<PregnancyStandard> getPregnancyStandard(Integer week, Integer fetusNumber) {
        return pregnancyStandardRepository.findByWeekAndFetusNumber(week, fetusNumber);
    }

    public Optional<MomStandard> getMomStandard(Integer week) {
        return momStandardRepository.findByWeek(week);
    }
}
