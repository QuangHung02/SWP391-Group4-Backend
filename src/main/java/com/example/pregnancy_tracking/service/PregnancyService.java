package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.PregnancyResponseDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.exception.ResourceNotFoundException;
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
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import com.example.pregnancy_tracking.dto.PregnancyListDTO;

@Service
public class PregnancyService {
    @Autowired
    private PregnancyRepository pregnancyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FetusRecordRepository fetusRecordRepository;

    @Autowired
    private FetusRepository fetusRepository;

    @Transactional
    public PregnancyListDTO createPregnancy(PregnancyDTO pregnancyDTO) {
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

        System.out.println("Creating fetuses for pregnancy: " + savedPregnancy.getPregnancyId());

        for (int i = 1; i <= pregnancyDTO.getTotalFetuses(); i++) {
            Fetus fetus = new Fetus();
            fetus.setPregnancy(savedPregnancy);
            fetus.setFetusIndex(i);
            fetus.setStatus(FetusStatus.ACTIVE);
            Fetus savedFetus = fetusRepository.save(fetus);
            System.out.println("Created fetus: " + savedFetus.getFetusId() + " with index: " + savedFetus.getFetusIndex());
        }

        List<Fetus> fetuses = fetusRepository.findByPregnancyPregnancyId(savedPregnancy.getPregnancyId());
        System.out.println("Found " + fetuses.size() + " fetuses for pregnancy");
        return convertToListDTO(savedPregnancy);
    }


    public PregnancyListDTO getPregnancyById(Long pregnancyId) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));
        return convertToListDTO(pregnancy);
    }

    @Transactional
    public PregnancyListDTO updatePregnancy(Long pregnancyId, PregnancyDTO pregnancyDTO) {
        if (pregnancyDTO.getGestationalWeeks() < 0 || pregnancyDTO.getGestationalDays() < 0) {
            throw new IllegalArgumentException("Gestational weeks and days must be non-negative.");
        }

        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Pregnancy not found"));

        LocalDate examDate = pregnancyDTO.getExamDate();
        int totalDays = (pregnancyDTO.getGestationalWeeks() * 7) + pregnancyDTO.getGestationalDays();
        LocalDate startDate = examDate.minusDays(totalDays);
        LocalDate dueDate = startDate.plusDays(280);

        int oldWeeks = pregnancy.getGestationalWeeks();
        
        pregnancy.setExamDate(examDate);
        pregnancy.setStartDate(startDate);
        pregnancy.setDueDate(dueDate);
        pregnancy.setGestationalWeeks(pregnancyDTO.getGestationalWeeks());
        pregnancy.setGestationalDays(pregnancyDTO.getGestationalDays());
        pregnancy.setLastUpdatedAt(LocalDateTime.now());

        List<FetusRecord> records = fetusRecordRepository.findByFetusPregnancyPregnancyId(pregnancyId);
        for (FetusRecord record : records) {
            int adjustedWeek = pregnancyDTO.getGestationalWeeks() - (oldWeeks - record.getWeek());
            record.setWeek(Math.max(adjustedWeek, 1));
        }
        fetusRecordRepository.saveAll(records);
        
        Pregnancy savedPregnancy = pregnancyRepository.save(pregnancy);
        return convertToListDTO(savedPregnancy);
    }

    public void updatePregnancyStatus(Long pregnancyId, PregnancyStatus newStatus) {
        System.out.println("Bắt đầu cập nhật trạng thái thai kỳ: " + pregnancyId + " thành " + newStatus);
        
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ"));
        System.out.println("Tìm thấy thai kỳ với trạng thái hiện tại: " + pregnancy.getStatus());
    
        pregnancy.setStatus(newStatus);
    
        List<Fetus> fetuses = fetusRepository.findByPregnancyPregnancyId(pregnancyId);
        System.out.println("Tìm thấy " + fetuses.size() + " thai nhi cần cập nhật");
    
        for (Fetus fetus : fetuses) {
            if (newStatus == PregnancyStatus.COMPLETED) {
                fetus.setStatus(FetusStatus.COMPLETED);
            } else if (newStatus == PregnancyStatus.ONGOING) {
                fetus.setStatus(FetusStatus.ACTIVE);
            }
            fetusRepository.save(fetus);
        }
    
        Pregnancy savedPregnancy = pregnancyRepository.save(pregnancy);
        System.out.println("Đã lưu thai kỳ với trạng thái mới: " + savedPregnancy.getStatus());
    }
    public List<PregnancyListDTO> getPregnanciesByUserId(Long userId) {
        List<Pregnancy> pregnancies = pregnancyRepository.findByUserId(userId);
        return pregnancies.stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    private PregnancyListDTO convertToListDTO(Pregnancy pregnancy) {
        PregnancyListDTO dto = new PregnancyListDTO();
        dto.setPregnancyId(pregnancy.getPregnancyId());
        dto.setUserId(pregnancy.getUser().getId());
        dto.setStartDate(pregnancy.getStartDate());
        dto.setDueDate(pregnancy.getDueDate());
        dto.setExamDate(pregnancy.getExamDate());
        dto.setGestationalWeeks(pregnancy.getGestationalWeeks());
        dto.setGestationalDays(pregnancy.getGestationalDays());
        dto.setStatus(pregnancy.getStatus());
        dto.setLastUpdatedAt(pregnancy.getLastUpdatedAt());
        dto.setCreatedAt(pregnancy.getCreatedAt());
        dto.setTotalFetuses(pregnancy.getTotalFetuses());
        
        List<PregnancyListDTO.FetusDTO> fetusDTOs = pregnancy.getFetuses().stream()
                .map(fetus -> {
                    PregnancyListDTO.FetusDTO fetusDTO = new PregnancyListDTO.FetusDTO();
                    fetusDTO.setFetusId(fetus.getFetusId());
                    fetusDTO.setFetusIndex(fetus.getFetusIndex());
                    fetusDTO.setStatus(fetus.getStatus());
                    return fetusDTO;
                })
                .collect(Collectors.toList());
        dto.setFetuses(fetusDTOs);
        
        return dto;
    }
    public PregnancyListDTO getOngoingPregnancyByUserId(Long userId) {
        Pregnancy pregnancy = pregnancyRepository.findByUserIdAndStatus(userId, PregnancyStatus.ONGOING)
                .orElseThrow(() -> new RuntimeException("No ongoing pregnancy found for user ID: " + userId));

        return convertToListDTO(pregnancy);
    }

    // Có thể xóa các DTO và method convert không cần thiết như PregnancyResponseDTO, convertToDTO
    public void updateFetusStatus(Long fetusId, FetusStatus status) {
        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new ResourceNotFoundException("Fetus not found with id: " + fetusId));
        
        fetus.setStatus(status);
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
