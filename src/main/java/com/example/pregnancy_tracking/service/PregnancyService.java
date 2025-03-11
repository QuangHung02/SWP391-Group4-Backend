package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.PregnancyResponseDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.FetusRecordRepository;
import com.example.pregnancy_tracking.repository.FetusRepository;
import com.example.pregnancy_tracking.repository.PregnancyRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import com.example.pregnancy_tracking.dto.PregnancyDTO;
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
    private FetusRecordService fetusRecordService;

    @Autowired
    private FetusRecordRepository fetusRecordRepository;

    @Autowired
    private FetusRepository fetusRepository;

    public Pregnancy createPregnancy(PregnancyDTO pregnancyDTO) {
        if (pregnancyDTO.getGestationalWeeks() < 0 || pregnancyDTO.getGestationalDays() < 0) {
            throw new IllegalArgumentException("Gestational weeks and days must be non-negative.");
        }

        User user = userRepository.findById(pregnancyDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasOngoingPregnancy = pregnancyRepository.existsByUserIdAndStatus(user.getId(), PregnancyStatus.ONGOING);
        if (hasOngoingPregnancy) {
            throw new IllegalStateException("User already has an ongoing pregnancy.");
        }

        LocalDate examDate = pregnancyDTO.getExamDate();
        int totalDays = (pregnancyDTO.getGestationalWeeks() * 7) + pregnancyDTO.getGestationalDays();
        LocalDate startDate = examDate.minusDays(totalDays);
        LocalDate dueDate = startDate.plusDays(280);

        user.setTotalPregnancies(user.getTotalPregnancies() + 1);
        userRepository.save(user);

        Pregnancy pregnancy = new Pregnancy();
        pregnancy.setUser(user);
        pregnancy.setExamDate(examDate);
        pregnancy.setStartDate(startDate);
        pregnancy.setDueDate(dueDate);
        pregnancy.setGestationalWeeks(pregnancyDTO.getGestationalWeeks());
        pregnancy.setGestationalDays(pregnancyDTO.getGestationalDays());
        pregnancy.setStatus(PregnancyStatus.ONGOING);
        pregnancy.setTotalFetuses(pregnancyDTO.getTotalFetuses());

        LocalDateTime now = LocalDateTime.now();
        pregnancy.setCreatedAt(now);
        pregnancy.setLastUpdatedAt(now);

        Pregnancy savedPregnancy = pregnancyRepository.save(pregnancy);

        for (int i = 1; i <= pregnancyDTO.getTotalFetuses(); i++) {
            Fetus fetus = new Fetus();
            fetus.setPregnancy(savedPregnancy);
            fetus.setFetusIndex(i);
            fetusRepository.save(fetus);
        }

        List<Fetus> fetuses = fetusRepository.findByPregnancyPregnancyId(savedPregnancy.getPregnancyId());
        if (fetuses.size() != pregnancyDTO.getTotalFetuses()) {
            throw new IllegalStateException("Mismatch: Expected " + pregnancyDTO.getTotalFetuses() +
                    " fetuses but found " + fetuses.size());
        }

        return savedPregnancy;
    }


    public Pregnancy getPregnancyById(Long pregnancyId) {
        return pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));
    }

    public Pregnancy updatePregnancy(Long pregnancyId, PregnancyDTO pregnancyDTO) {
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
        fetusRecordService.updateRecordsForPregnancy(pregnancyId, pregnancy.getGestationalWeeks());

        return pregnancy;
    }

    public void updatePregnancyStatus(Long pregnancyId, PregnancyStatus newStatus) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));

        pregnancy.setStatus(newStatus);

        List<Fetus> fetuses = fetusRepository.findByPregnancyPregnancyId(pregnancyId);
        for (Fetus fetus : fetuses) {
            if (newStatus == PregnancyStatus.COMPLETED) {
                fetus.setStatus(FetusStatus.COMPLETED);
            } else if (newStatus == PregnancyStatus.ONGOING) {
                fetus.setStatus(FetusStatus.ACTIVE);
            }
            fetusRepository.save(fetus);
        }

        pregnancyRepository.save(pregnancy);
    }
    public List<Pregnancy> getPregnanciesByUserId(Long userId) {
        return pregnancyRepository.findByUserId(userId);
    }
    public PregnancyResponseDTO getOngoingPregnancyByUserId(Long userId) {
        Pregnancy pregnancy = pregnancyRepository.findByUserIdAndStatus(userId, PregnancyStatus.ONGOING)
                .orElseThrow(() -> new RuntimeException("No ongoing pregnancy found for user ID: " + userId));

        return convertToDTO(pregnancy);
    }
    public void updateFetusStatus(Long fetusId, FetusStatus newStatus) {
        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Fetus not found"));
        fetus.setStatus(newStatus);
        fetusRepository.save(fetus);
    }
    private PregnancyResponseDTO convertToDTO(Pregnancy pregnancy) {
        PregnancyResponseDTO dto = new PregnancyResponseDTO();
        dto.setPregnancyId(pregnancy.getPregnancyId());
        dto.setUserId(pregnancy.getUser().getId());
        dto.setStartDate(pregnancy.getStartDate());
        dto.setDueDate(pregnancy.getDueDate());
        dto.setExamDate(pregnancy.getExamDate());
        dto.setGestationalWeeks(pregnancy.getGestationalWeeks());
        dto.setGestationalDays(pregnancy.getGestationalDays());
        dto.setStatus(pregnancy.getStatus());
        dto.setFetuses(pregnancy.getFetuses());
        return dto;
    }


}
