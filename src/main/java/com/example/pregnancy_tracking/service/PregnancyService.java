package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.exception.ResourceNotFoundException;
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
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
import java.time.temporal.ChronoUnit;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import java.util.ArrayList;

@Slf4j
@Service
public class PregnancyService {
    @Autowired
    private MembershipService membershipService;

    @Autowired
    private PregnancyRepository pregnancyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FetusRecordService fetusRecordService;

    @Autowired
    private FetusRepository fetusRepository;

    @Autowired
    private StandardService standardService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReminderRepository reminderRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoUpdatePregnancyStatus() {
        LocalDate today = LocalDate.now();
        List<Pregnancy> duePregnancies = pregnancyRepository.findByStatusAndDueDateLessThanEqual(
            PregnancyStatus.ONGOING, 
            today
        );
    
        for (Pregnancy pregnancy : duePregnancies) {
            try {
                updatePregnancyStatus(pregnancy.getPregnancyId(), PregnancyStatus.COMPLETED);
                
                String title = "Thai kỳ đã hoàn thành";
                String body = "Thai kỳ của bạn đã đến ngày dự sinh và được tự động đánh dấu hoàn thành";
                notificationService.sendHealthAlertNotification(
                    pregnancy.getUser().getId(),
                    title,
                    body
                );
                
                log.info("Auto completed pregnancy ID: {} due to reaching due date: {}", 
                    pregnancy.getPregnancyId(), pregnancy.getDueDate());
            } catch (Exception e) {
                log.error("Error processing pregnancy ID: {} - {}", 
                    pregnancy.getPregnancyId(), e.getMessage());
            }
        }
    }
    @Transactional
    public PregnancyListDTO createPregnancy(PregnancyDTO pregnancyDTO) {
        if (!membershipService.canCreatePregnancyRecord(pregnancyDTO.getUserId())) {
            throw new MembershipFeatureException("Bạn cần đăng ký gói thành viên để tạo thai kỳ");
        }

        if (pregnancyDTO.getGestationalWeeks() < 0 || pregnancyDTO.getGestationalDays() < 0) {
            throw new IllegalArgumentException("Tuần thai và ngày thai không được âm");
        }

        User user = userRepository.findById(pregnancyDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        boolean hasOngoingPregnancy = pregnancyRepository.existsByUserIdAndStatus(user.getId(), PregnancyStatus.ONGOING);
        if (hasOngoingPregnancy) {
            throw new IllegalStateException("Người dùng đã có thai kỳ đang theo dõi");
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

        if (membershipService.canAccessStandardFeatures(user.getId())) {
            for (int week = 1; week <= pregnancyDTO.getGestationalWeeks(); week++) {
                standardService.checkAndCreateWeeklyTasks(
                    user.getId(),
                    savedPregnancy.getPregnancyId(),
                    week
                );
            }
        }

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ"));
        return convertToListDTO(pregnancy);
    }

    @Transactional
    public PregnancyListDTO updatePregnancy(Long pregnancyId, PregnancyDTO pregnancyDTO) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ"));
            
        if (!membershipService.canCreatePregnancyRecord(pregnancy.getUser().getId())) {
            throw new MembershipFeatureException("Bạn cần đăng ký gói thành viên để cập nhật thai kỳ");
        }

        if (pregnancy.getStatus() != PregnancyStatus.ONGOING) {
            throw new IllegalStateException("Chỉ có thể cập nhật thai kỳ đang theo dõi");
        }

        if (pregnancyDTO.getGestationalWeeks() <= pregnancy.getGestationalWeeks()) {
            throw new IllegalArgumentException("Tuần thai mới phải lớn hơn tuần thai hiện tại");
        }
        if (pregnancyDTO.getGestationalWeeks() > 40) {
            throw new IllegalArgumentException("Tuần thai không được vượt quá 40 tuần");
        }
        if (pregnancyDTO.getGestationalDays() < 0 || pregnancyDTO.getGestationalDays() > 6) {
            throw new IllegalArgumentException("Ngày thai phải từ 0 đến 6 ngày");
        }

        LocalDate lastExamDate = pregnancy.getExamDate();
        int oldWeeks = pregnancy.getGestationalWeeks();
        LocalDate examDate = pregnancyDTO.getExamDate();

        // Luôn cập nhật lại ngày tháng khi có thay đổi tuần thai
        int totalDays = (pregnancyDTO.getGestationalWeeks() * 7) + pregnancyDTO.getGestationalDays();
        LocalDate startDate = examDate.minusDays(totalDays);
        LocalDate dueDate = startDate.plusDays(280);
        
        pregnancy.setStartDate(startDate);
        pregnancy.setDueDate(dueDate);
        pregnancy.setLastExamDate(lastExamDate);
        pregnancy.setExamDate(examDate);
        pregnancy.setGestationalWeeks(pregnancyDTO.getGestationalWeeks());
        pregnancy.setGestationalDays(pregnancyDTO.getGestationalDays());
        pregnancy.setLastUpdatedAt(LocalDateTime.now());

        Pregnancy savedPregnancy = pregnancyRepository.save(pregnancy);
        
        // Cập nhật các records nếu tuần thai thay đổi
        if (pregnancyDTO.getGestationalWeeks() != oldWeeks) {
            fetusRecordService.updateRecordsForPregnancy(
                pregnancyId, 
                examDate,
                lastExamDate,
                pregnancyDTO.getGestationalWeeks(),
                oldWeeks
            );
        }

        if (membershipService.canAccessStandardFeatures(pregnancy.getUser().getId())) {
            standardService.checkAndCreateWeeklyTasks(
                pregnancy.getUser().getId(),
                savedPregnancy.getPregnancyId(),
                pregnancyDTO.getGestationalWeeks()
            );
        }

        return convertToListDTO(savedPregnancy);
    }

    @Transactional
    public void updatePregnancyStatus(Long pregnancyId, PregnancyStatus newStatus) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ!"));
    
        if (pregnancy.getStatus() == PregnancyStatus.COMPLETED || 
            pregnancy.getStatus() == PregnancyStatus.CANCEL) {
            throw new IllegalStateException("Không thể thay đổi trạng thái của thai kỳ đã hoàn thành hoặc đã hủy!");
        }
    
        if (pregnancy.getStatus() == PregnancyStatus.ONGOING) {
            pregnancy.setStatus(newStatus);
            
            List<Fetus> fetuses = fetusRepository.findByPregnancyPregnancyId(pregnancyId);
            for (Fetus fetus : fetuses) {
                if (newStatus == PregnancyStatus.COMPLETED) {
                    fetus.setStatus(FetusStatus.COMPLETED);
                } else if (newStatus == PregnancyStatus.CANCEL) {
                    fetus.setStatus(FetusStatus.CANCEL);
                }
                fetusRepository.save(fetus);
            }
    
            List<Reminder> reminders = reminderRepository.findByPregnancy_PregnancyId(pregnancyId);
            int totalReminders = reminders.size();
            
            reminderRepository.deleteByPregnancyPregnancyId(pregnancyId);
            
            if (totalReminders > 0) {
                String title = "Nhắc nhở đã được xóa";
                String body = String.format("Đã xóa %d nhắc nhở do thai kỳ đã %s", 
                    totalReminders, 
                    newStatus == PregnancyStatus.COMPLETED ? "hoàn thành" : "hủy");
                notificationService.sendHealthAlertNotification(
                    pregnancy.getUser().getId(),
                    title,
                    body
                );
            }
            
            pregnancyRepository.save(pregnancy);
        } else {
            throw new IllegalStateException("Chỉ có thể thay đổi trạng thái của thai kỳ đang theo dõi!");
        }
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ đang theo dõi cho người dùng ID: " + userId));

        return convertToListDTO(pregnancy);
    }
    
    public void updateFetusStatus(Long fetusId, FetusStatus status) {
        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thai nhi với id: " + fetusId));
        
        if (fetus.getStatus() == FetusStatus.COMPLETED || 
            fetus.getStatus() == FetusStatus.CANCEL) {
            throw new IllegalStateException("Không thể thay đổi trạng thái của thai nhi đã hoàn thành hoặc đã hủy!");
        }

        if (fetus.getPregnancy().getStatus() != PregnancyStatus.ONGOING) {
            throw new IllegalStateException("Không thể thay đổi trạng thái thai nhi khi thai kỳ không còn theo dõi!");
        }
        fetus.setStatus(status);
        fetusRepository.save(fetus);
    }

    @Transactional
    public void deleteUserPregnancies(Long userId) {
        pregnancyRepository.deleteByUserId(userId);
    }
}
