package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.PregnancyDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.FetusRepository;
import com.example.pregnancy_tracking.repository.FetusRecordRepository;
import com.example.pregnancy_tracking.repository.PregnancyRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PregnancyService {
    @Autowired
    private PregnancyRepository pregnancyRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FetusRecordService pregnancyRecordService;

public Pregnancy createPregnancy(PregnancyDTO pregnancyDTO) {
    if (pregnancyDTO.getGestationalWeeks() < 0 || pregnancyDTO.getGestationalDays() < 0) {
        throw new IllegalArgumentException("Gestational weeks and days must be non-negative.");
    }

        User user = userRepository.findById(pregnancyDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate examDate = pregnancyDTO.getExamDate();
        int totalDays = (pregnancyDTO.getGestationalWeeks() * 7) + pregnancyDTO.getGestationalDays();

        LocalDate startDate = examDate.minusDays(totalDays);
        LocalDate dueDate = startDate.plusDays(280);

        user.setTotalPregnancies(user.getTotalPregnancies() + 1);
        userRepository.save(user);

        Pregnancy pregnancy = new Pregnancy();
        pregnancy.setUser(user);
        pregnancy.setStartDate(startDate);
        pregnancy.setDueDate(dueDate);
        pregnancy.setGestationalWeeks(pregnancyDTO.getGestationalWeeks());
        pregnancy.setGestationalDays(pregnancyDTO.getGestationalDays());
        pregnancy.setStatus(PregnancyStatus.ONGOING);

        LocalDateTime now = LocalDateTime.now();
        pregnancy.setCreatedAt(now);
        pregnancy.setLastUpdatedAt(now);

        return pregnancyRepository.save(pregnancy);
    }
    public Pregnancy getPregnancyById(Long pregnancyId) {
        return pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));
    }
public Pregnancy updatePregnancy(Long pregnancyId, PregnancyDTO pregnancyDTO) {
    // Validate the input data
    if (pregnancyDTO.getGestationalWeeks() < 0 || pregnancyDTO.getGestationalDays() < 0) {
        throw new IllegalArgumentException("Gestational weeks and days must be non-negative.");
    }

        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));

        LocalDate examDate = pregnancyDTO.getExamDate();
        int totalDays = (pregnancyDTO.getGestationalWeeks() * 7) + pregnancyDTO.getGestationalDays();

        LocalDate startDate = examDate.minusDays(totalDays);
        LocalDate dueDate = startDate.plusDays(280);

        pregnancy.setExamDate(examDate);
        pregnancy.setStartDate(startDate);
        pregnancy.setDueDate(dueDate);
        pregnancy.setGestationalWeeks(pregnancyDTO.getGestationalWeeks());
        pregnancy.setGestationalDays(pregnancyDTO.getGestationalDays());
        pregnancy.setLastUpdatedAt(LocalDateTime.now());

        pregnancyRepository.save(pregnancy);

        pregnancyRecordService.updateRecordsForPregnancy(pregnancyId, pregnancy.getGestationalWeeks());

        return pregnancy;
    }

    @Autowired
    private FetusRecordRepository pregnancyRecordRepository;
    @Autowired
    private FetusRepository fetusRepository;

    public Pregnancy updatePregnancyStatus(Long pregnancyId, PregnancyStatus newStatus) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));

        pregnancy.setStatus(newStatus);
        pregnancy.setLastUpdatedAt(LocalDateTime.now());

        if (newStatus == PregnancyStatus.COMPLETED) {
            List<Fetus> fetuses = fetusRepository.findByPregnancyPregnancyId(pregnancyId);
            for (Fetus fetus : fetuses) {
                List<FetusRecord> records = pregnancyRecordRepository.findByFetusFetusId(fetus.getFetusId());
                for (FetusRecord record : records) {
                    if (record.getStatus() == FetusRecordStatus.ACTIVE) {
                        record.setStatus(FetusRecordStatus.COMPLETED);
                        pregnancyRecordRepository.save(record);
                    }
                }
            }
        }

        return pregnancyRepository.save(pregnancy);
    }
}
